package com.sturdycobble.createrevision.contents.heat;

public interface HeatContainer {
	
	public abstract double getTemp();
	public abstract void setTemp(double tempIn);
	public abstract double getCapacity();
	public abstract double getConductivity();

}
