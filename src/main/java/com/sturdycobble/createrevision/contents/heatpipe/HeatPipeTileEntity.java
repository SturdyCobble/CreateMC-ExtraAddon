package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.contents.heatSystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatSystem.HeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.Utils;
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

	private LazyOptional<HeatContainer> heatContainer;

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return this.heatContainer.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		HeatContainer h = Utils.getHeatContainer(this);
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for(Direction d : Direction.values()){
			mpos.setPos(pos).move(d);
			HeatContainer ex = Utils.getHeatContainer(this.world.getTileEntity(mpos));
			h.exchangeHeat(ex);
		}
	}




}
