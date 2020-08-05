package com.sturdycobble.createrevision.contents.heat;

public interface HeatContainer {

    void applyHeat(double heat);

    void setTemp(double temp);

    double getHeatEnergy();

    double getHeatCapacity();

    double getTemp();

    double getConductivity();
}
