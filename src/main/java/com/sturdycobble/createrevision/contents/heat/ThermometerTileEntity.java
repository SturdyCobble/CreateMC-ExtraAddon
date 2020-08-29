package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.LinkedList;
import java.util.List;

import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils.FacingDistance;

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
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing);
			if (heatContainer.isPresent()) {
				return heatContainer.orElse(null).getTemp();
			}
		}
		return -1;
	}

	public List<FacingDistance> getNodes() {
		List<FacingDistance> nodes = new LinkedList<FacingDistance>();
		Direction facing = world.getBlockState(pos).get(FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing);
			if (heatContainer.isPresent()) {
				if (((IHeatableTileEntity) te).isNode() == true)
					nodes.add(new FacingDistance(Direction.UP, 0L));
				for (FacingDistance distVector : ((IHeatableTileEntity) te).getNeighborMap().values())
					nodes.add(distVector);
			}
		}
		return nodes;
	}

}
