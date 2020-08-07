package com.sturdycobble.createrevision.contents.geo.fan;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class GeothermalFanTileEntity extends GeneratingKineticTileEntity {

	protected boolean isGenerator;

	public GeothermalFanTileEntity() {
		super(ModTileEntityTypes.GEOTHERMAL_FAN.get());
		isGenerator = false;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		isGenerator = compound.getBoolean("Generating");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("Generating", isGenerator);
		return super.write(compound);
	}

	@Override
	public float calculateStressApplied() {
		return isGenerator ? 0 : 8;
	}

	@Override
	public float calculateAddedStressCapacity() {
		return isGenerator ? 256 : 0;
	}

	@Override
	public float getGeneratedSpeed() {
		return isGenerator ? 16 * withCoolSource() : 0;
	}

	private int withCoolSource() {
		int orgX = pos.getX();
		int orgY = pos.getY();
		int orgZ = pos.getZ();

		for (int parmX = orgX - 1; parmX <= orgX + 1; parmX++) {
			for (int parmZ = orgZ - 1; parmZ <= orgZ + 1; parmZ++) {
				BlockPos.Mutable focusedPos = new BlockPos.Mutable(parmX, orgY, parmZ);
				if (world.getBlockState(focusedPos).getBlock() == Blocks.PACKED_ICE.getBlock())
					return 2;
			}
		}
		return 1;
	}

	public void updateGenerator(Direction facing) {
		boolean shouldGenerate = world.isBlockPowered(pos) && facing == Direction.DOWN
				&& world.isBlockPresent(pos.down()) && isBelowHot();
		if (shouldGenerate == isGenerator)
			return;

		isGenerator = shouldGenerate;
		updateGeneratedRotation();
	}

	public boolean isBelowHot() {
		if (world == null)
			return false;
		BlockState checkState = world.getBlockState(pos.down());

		if ((checkState.getBlock() == Blocks.LAVA) && pos.getY() < 15)
			return true;
		return false;
	}

	@Override
	public void tick() {
	}
}