package com.sturdycobble.createrevision.contents.heat;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface IHeatableTileEntity extends ICapabilityProvider {
	
	public abstract void markConnection();
	public abstract Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> findNeighborNode();
	public abstract void updateConnection();
	public abstract boolean isNode();
	public abstract Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> getNeighborMap();
	public abstract double getConductivity();
	
	default double getHeatCurrent(Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> neighbors, double tempIn) {
		double heatCurrent = 0;
		for( IHeatableTileEntity node : neighbors.keySet()) {
			Long distance = neighbors.get(node).getValue();
			Direction facingDirection = neighbors.get(node).getKey().getOpposite();
			if ( node != null) {
				LazyOptional<HeatContainer> nodeHeatContainer = node.getCapability(CapabilityHeat.HEAT_CAPABILITY, facingDirection);
				if (nodeHeatContainer.isPresent()) {
					double neighborTemp = nodeHeatContainer.orElse(null).getTemp();
					heatCurrent += (neighborTemp - tempIn)*getConductivity()/distance;
				}
			}
		}
		
		return heatCurrent;
	}
	
}
