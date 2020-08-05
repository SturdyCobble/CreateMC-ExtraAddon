package com.sturdycobble.createrevision.contents.heatSystem;

public interface HeatContainer {

    void exchangeHeat(HeatContainer ex);

    void applyHeat(double heat);

    void setTemp(double temp);

    double getHeatEnergy();

    double getHeatCapacity();

    double getTemp();

    double getConductivity();
}
