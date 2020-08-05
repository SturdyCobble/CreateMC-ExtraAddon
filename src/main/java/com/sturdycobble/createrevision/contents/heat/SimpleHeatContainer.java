package com.sturdycobble.createrevision.contents.heat;

public class SimpleHeatContainer implements HeatContainer {
    protected double heatCapacity;
    protected double temp;
    protected double conductivity;

    @Override
    public void applyHeat(double heat) {
        temp = (getHeatEnergy()+heat) < 0  ? 0 : (getHeatEnergy()+heat)/heatCapacity;
    }

    @Override
    public double getHeatEnergy() {
        return heatCapacity*temp;
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
