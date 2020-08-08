package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatsystem.HeatContainer;
import com.sturdycobble.createrevision.contents.heatsystem.IHeatableTileEntity;
import com.sturdycobble.createrevision.contents.heatsystem.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeatPipeTileEntity extends TileEntity implements IHeatableTileEntity {
	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(30, 300, 10));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainer.cast();
		}
		return super.getCapability(cap,null);
	}


	@Override
	public TileEntity getTE() {
		return this.getTileEntity();
	}
}
