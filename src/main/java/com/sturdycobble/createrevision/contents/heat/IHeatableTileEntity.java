package com.sturdycobble.createrevision.contents.heat;

public interface IHeatableTileEntity {
	
	public abstract double getTemp();
	public abstract void setTemp(int tempIn);
	public abstract double getCapacity();
	public abstract double getPower();
	public abstract void markConnection();

}
