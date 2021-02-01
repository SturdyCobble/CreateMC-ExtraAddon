package com.sturdycobble.createrevision.api.heat;

public interface WritableHeatContainer extends HeatContainer {

	void setTemp(double temp);
	
	default void addHeat(double heat) {
		setTemp(getTemp() + heat/getCapacity());
	}
	
}
