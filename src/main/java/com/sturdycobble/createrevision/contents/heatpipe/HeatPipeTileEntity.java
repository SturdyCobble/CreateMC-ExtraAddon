package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatsystem.HeatContainer;
import com.sturdycobble.createrevision.contents.heatsystem.HeatUtils;
import com.sturdycobble.createrevision.contents.heatsystem.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatPipeTileEntity extends TileEntity implements ITickableTileEntity, ICapabilityProvider {
	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(30, 300, 1));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY && this.heatContainer != null) {
			return (LazyOptional<T>) this.heatContainer;
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (world.getWorld().getWorldInfo().getGameTime() % 100 == 0) {
			HeatContainer h = this.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null);
			if (h != null) {
				BlockPos.Mutable mpos = new BlockPos.Mutable();
				for (Direction d : Direction.values()) {
					mpos.setPos(pos).move(d);
					TileEntity te = this.world.getTileEntity(mpos);
					if (te != null) {
						HeatContainer ex = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null);
						if (ex != null) {
							HeatUtils.exchangeHeat(h,ex);
							System.out.println("heatex");
							this.sendData();
							markDirty();
						}
					}
				}
			}
		}
	}

	public void sendData(){
		this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 22);
	}
}
