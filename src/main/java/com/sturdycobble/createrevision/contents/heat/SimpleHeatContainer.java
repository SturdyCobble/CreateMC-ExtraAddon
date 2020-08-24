package com.sturdycobble.createrevision.contents.heat;

public class SimpleHeatContainer implements HeatContainer {
	
	private double temp;
	private double heatCapacity;
	private double conductivity;
	
	public SimpleHeatContainer(double tempIn, double capac, double condt) {
		heatCapacity = capac;
		temp = tempIn;
		conductivity = condt;
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

	@Override
	public double getConductivity() {
		return conductivity;
	}
	

}
