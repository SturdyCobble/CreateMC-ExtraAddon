package com.sturdycobble.createrevision.contents.geo.fan;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Geothermal Fan Tile Entity
 * Modified version of Encased Fan(com.simibubi.create.content.contraptions.components.fan.EncasedFanTileEntity)
 *
 * @author SturdyCobble
 */
public class GeothermalFanTileEntity extends GeneratingKineticTileEntity implements ICapabilityProvider {

	protected boolean update;

	public GeothermalFanTileEntity() {
		super(ModTileEntityTypes.GEOTHERMAL_FAN.get());
		update = true;
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
		if (blockBelowIsHot() == false)
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