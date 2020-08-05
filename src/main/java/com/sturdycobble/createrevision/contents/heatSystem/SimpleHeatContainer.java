package com.sturdycobble.createrevision.contents.heatSystem;

public class SimpleHeatContainer implements HeatContainer {
	protected double heatCapacity;
	protected double temp;
	protected double conductivity;

	public SimpleHeatContainer(double cap, double temp,double con){
		this.heatCapacity=cap;
		this.temp=temp;
		this.conductivity=con;
	}

	@Override
	public void exchangeHeat(HeatContainer ex) {
		if (this.getTemp() > ex.getTemp()) {
			double transHeat = this.getConductivity() * ex.getConductivity() * (this.getTemp() - ex.getTemp());
			this.applyHeat(-transHeat);
			ex.applyHeat(transHeat);
		}
	}

	@Override
	public void applyHeat(double heat) {
		temp = (getHeatEnergy() + heat) < 0 ? 0 : (getHeatEnergy() + heat) / heatCapacity;
	}

	@Override
	public double getHeatEnergy() {
		return heatCapacity * temp;
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
