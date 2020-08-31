package com.sturdycobble.createrevision.api.heat;

public class SimpleHeatContainer implements HeatContainer {

	private double temp = 300;
	private double heatCapacity;

	public SimpleHeatContainer(double capc) {
		heatCapacity = capc;
	}

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

}
