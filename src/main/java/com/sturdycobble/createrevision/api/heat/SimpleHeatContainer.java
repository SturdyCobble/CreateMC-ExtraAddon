package com.sturdycobble.createrevision.api.heat;

public class SimpleHeatContainer implements HeatContainer {
	
	private double temp;
	private double heatCapacity;
	
	public SimpleHeatContainer(double tempIn, double capc) {
		heatCapacity = capc;
		temp = tempIn;
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
