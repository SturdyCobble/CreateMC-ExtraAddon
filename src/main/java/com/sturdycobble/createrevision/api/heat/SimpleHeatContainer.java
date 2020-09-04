package com.sturdycobble.createrevision.api.heat;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.INBTSerializable;

public class SimpleHeatContainer implements HeatContainer, INBTSerializable<CompoundNBT> {

	private double temp = 300;
	private double heatCapacity = 1; 
	private Map<Direction, Integer> neighborMap = new HashMap<Direction, Integer>(6);
	
	@Override
	public double getTemp() {
		return temp;
	}

	@Override
	public void setTemp(double tempIn) {
		temp = tempIn;
	}

	@Override
	public double getCapacity() {
		return heatCapacity;
	}
	
	public void setCapacity(double cap) {
		heatCapacity = cap;
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("temp", getTemp());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setTemp(nbt.getDouble("temp"));
	}
	
	@Override
	public void addHeat(double heatCurrent) {
		setTemp(getTemp() + heatCurrent / getCapacity());
	}
	
	@Override
	public Map<Direction, Integer> getNeighbors() {
		return neighborMap;
	}
	
	public void setNeighbors(Map<Direction, Integer> neighbors) {
		for (Direction d : Direction.values()) {
			if (neighbors.containsKey(d)) {
				neighborMap.put(d, neighbors.get(d));
			} else {
				neighborMap.remove(d);
			}
		}
	}

	public void putNeighbor(Direction direction, int distance) {
		neighborMap.put(direction, distance);
	}
	
	public void removeNeighbor(Direction direction) {
		neighborMap.remove(direction);
	}

}