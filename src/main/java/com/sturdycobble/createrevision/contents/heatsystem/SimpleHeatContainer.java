package com.sturdycobble.createrevision.contents.heatsystem;

public class SimpleHeatContainer implements HeatContainer {
	protected double heatCapacity;
	protected double temp;
	protected double conductivity;

	public SimpleHeatContainer(double cap, double temp, double con) {
		this.heatCapacity = cap;
		this.temp = temp;
		this.conductivity = con;
	}

	@Override
	public double getHeatCapacity() {
		return heatCapacity;
	}

	@Override
	public double getTemp() {
		return temp;
	}

	@Override
	public double getConductivity() {
		return conductivity;
	}

	@Override
	public void setTemp(double temp) {
		this.temp = temp;
	}
}
