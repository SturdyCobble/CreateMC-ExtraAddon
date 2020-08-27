package com.sturdycobble.createrevision.contents.heat.transfer;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

import com.sturdycobble.createrevision.contents.heat.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heat.HeatContainer;
import com.sturdycobble.createrevision.contents.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class ThermometerTileEntity extends TileEntity {

	public ThermometerTileEntity() {
		super(ModTileEntityTypes.THERMOMETER.get());
	}

	public double getTemp() {
		Direction facing = world.getBlockState(pos).get(ThermometerBlock.FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
			if (heatContainer.isPresent()) {
				return heatContainer.orElse(null).getTemp();		
			}
		}
		return -1;
	}
	
	public List<SimpleEntry<Direction, Long>> getNodes() {
		List<SimpleEntry<Direction, Long>> nodes = new LinkedList<SimpleEntry<Direction, Long>>();
		Direction facing = world.getBlockState(pos).get(ThermometerBlock.FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
			if (heatContainer.isPresent()) {
				if (((IHeatableTileEntity) te).isNode() == true)
					nodes.add(new SimpleEntry<Direction, Long>(Direction.UP, 0L));
				for (SimpleEntry<Direction, Long> distVector : ((IHeatableTileEntity) te).getNeighborMap().values())
					nodes.add(distVector);			
			}
		}
		return nodes;
	}
	
}
