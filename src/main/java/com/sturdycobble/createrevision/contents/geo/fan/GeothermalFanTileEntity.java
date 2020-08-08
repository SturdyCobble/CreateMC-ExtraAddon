package com.sturdycobble.createrevision.contents.geo.fan;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatsystem.HeatContainer;
import com.sturdycobble.createrevision.contents.heatsystem.IHeatableTileEntity;
import com.sturdycobble.createrevision.contents.heatsystem.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Geothermal Fan Tile Entity
 * Modified version of Encased Fan(com.simibubi.create.content.contraptions.components.fan.EncasedFanTileEntity)
 *
 * @author SturdyCobble
 */
public class GeothermalFanTileEntity extends GeneratingKineticTileEntity implements IHeatableTileEntity {

	protected boolean update;

	public GeothermalFanTileEntity() {
		super(ModTileEntityTypes.GEOTHERMAL_FAN.get());
		update = true;
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(300, 400, 10));

	@Override
	public TileEntity getTE() {
		return this;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainer.cast();
		}
		return super.getCapability(cap,null);
	}

	@Override
	public float calculateAddedStressCapacity() {
		return 256;
	}

	@Override
	public float getGeneratedSpeed() {
		return 16 * withCoolSource();
	}

	private int withCoolSource() {
		int orgX = pos.getX();
		int orgY = pos.getY();
		int orgZ = pos.getZ();

		for (int parmX = orgX - 1; parmX <= orgX + 1; parmX++) {
			for (int parmZ = orgZ - 1; parmZ <= orgZ + 1; parmZ++) {
				BlockPos focusedPos = new BlockPos(parmX, orgY, parmZ);
				if (world.getBlockState(focusedPos).getBlock() == Blocks.PACKED_ICE.getBlock()) return 2;
			}
		}
		return 1;
	}


	public void updateGenerator() {
		if (!blockBelowIsHot())
			return;

		updateGeneratedRotation();
	}

	public boolean blockBelowIsHot() {
		if (world == null)
			return false;
		BlockState checkState = world.getBlockState(pos.down());

		if (!(checkState.getBlock() == Blocks.LAVA) || pos.getY() > 15)
			return false;

		return true;
	}

	public void blockBelowChanged() {
		update = true;
	}

	@Override
	public void tick() {
		if (update) {
			update = false;
			updateGenerator();
		}
	}
}