package com.sturdycobble.createrevision.contents.heatsystem;

import com.sturdycobble.createrevision.contents.heatpipe.HeatPipeBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

//test
public interface IHeatableTileEntity extends ITickableTileEntity, ICapabilityProvider {

	@Override
	default void tick() {
		if (this.getTE().getWorld().isRemote() && this.getTE().getWorld().getWorldInfo().getGameTime() % 20 == 0) {
			LazyOptional<HeatContainer> cap1 = this.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
			BlockPos.Mutable mpos = new BlockPos.Mutable();
			for (Direction d : Direction.values()) {
				mpos.setPos(this.getTE().getPos()).move(d);
				TileEntity te = this.getTE().getWorld().getTileEntity(mpos);
				if (te != null) {
					LazyOptional<HeatContainer> cap2 = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
					if (cap1.isPresent() && cap2.isPresent())
						HeatUtils.exchangeHeat(cap1.orElse(null), cap2.orElse(null));
				}
			}
		}
	}

	TileEntity getTE();
}
