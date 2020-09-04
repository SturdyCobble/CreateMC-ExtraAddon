package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.LinkedList;
import java.util.List;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class ThermometerTileEntity extends TileEntity {

	public ThermometerTileEntity() {
		super(ModTileEntityTypes.THERMOMETER.get());
	}

	public double getTemp() {
		Direction facing = world.getBlockState(pos).get(FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		double temp = -1;
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing);
			if (heatContainer.isPresent()) {
				temp = heatContainer.orElse(null).getTemp();
			}
		}
		return temp;
	}
	
	public List<Integer> getNodes() {
		List<Integer> nodes = new LinkedList<Integer>();
		Direction facing = getBlockState().get(FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing);
			if (heatContainer.isPresent()) {
				CreateRevision.LOGGER.info("CONTAINER "+heatContainer.orElse(null).getTemp()+" // "+heatContainer.orElse(null).getNeighbors());
				nodes.addAll(heatContainer.orElse(null).getNeighbors().values());
				if (te instanceof HeatPipeTileEntity) {
					if (((HeatPipeTileEntity) te).isNode()) {
						nodes.add(0);
					}
				}
			}
		}
		CreateRevision.LOGGER.info("CONTAINER "+nodes);
		return nodes;
	}
	
}
