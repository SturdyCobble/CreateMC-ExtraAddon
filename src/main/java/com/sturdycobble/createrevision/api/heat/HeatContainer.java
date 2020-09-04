package com.sturdycobble.createrevision.api.heat;

import java.util.Map;

import net.minecraft.util.Direction;

public interface HeatContainer {

	double getTemp();
	void setTemp(double temp);
	double getCapacity();
	void addHeat(double heat);
	public Map<Direction, Integer> getNeighbors();

}
