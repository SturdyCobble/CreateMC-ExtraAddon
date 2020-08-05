package com.sturdycobble.createrevision.contents.heatsystem;

public interface HeatContainer {

    default void exchangeHeat(HeatContainer ex) {
        if (this.getTemp() > ex.getTemp()) {
            double transHeat = this.getConductivity() * ex.getConductivity() * (this.getTemp() - ex.getTemp());
            this.applyHeat(-transHeat);
            ex.applyHeat(transHeat);
        }
    }

    default void applyHeat(double heat) {
        this.setTemp((getHeatEnergy() + heat) < 0 ? 0 : (getHeatEnergy() + heat) / this.getHeatCapacity());
    }

    default double getHeatEnergy(){
        return getTemp()*getHeatCapacity();
    }

    void setTemp(double temp);

    double getTemp();

    double getHeatCapacity();

    double getConductivity();
}
