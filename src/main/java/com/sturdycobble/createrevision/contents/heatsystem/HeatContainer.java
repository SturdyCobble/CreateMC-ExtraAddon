package com.sturdycobble.createrevision.contents.heatsystem;

public interface HeatContainer {

	default void applyHeat(double heat) {
		this.setTemp( (getHeatEnergy() + heat) < 0 ? 0 : (getHeatEnergy() + heat) / this.getHeatCapacity() );
	}

	default double getHeatEnergy() {
		return getTemp() * getHeatCapacity();
	}

	void setTemp(double temp);

	double getTemp();

	double getHeatCapacity();

	double getConductivity();
}
