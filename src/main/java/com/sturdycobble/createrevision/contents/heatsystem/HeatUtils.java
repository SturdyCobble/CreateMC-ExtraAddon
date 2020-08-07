package com.sturdycobble.createrevision.contents.heatsystem;

public class HeatUtils {
	public static void exchangeHeat(HeatContainer from, HeatContainer to) {
		if ((int) from.getTemp() > (int) to.getTemp()) {
			System.out.println("START");
			System.out.println("    FROM : " + from.getTemp());
			System.out.println("    TO : " + to.getTemp());

			double transHeat = from.getConductivity() * to.getConductivity() * (from.getTemp() - to.getTemp());
			double fromTemp = (from.getHeatEnergy() - transHeat) / from.getHeatCapacity();
			double toTemp = (to.getHeatEnergy() + transHeat) / to.getHeatCapacity();
			if ((int) fromTemp >= (int) toTemp) {
				from.setTemp(fromTemp);
				to.setTemp(toTemp);
				System.out.println("RESULT : HEAT_TRANSFER");
			} else {
				double temp = (to.getHeatEnergy() + from.getHeatEnergy()) / (to.getHeatCapacity() + from.getHeatCapacity());
				from.setTemp(temp);
				to.setTemp(temp);
				System.out.println("RESULT : HEAT_BALANCE");
			}

			System.out.println("    FROM : " + from.getTemp());
			System.out.println("    TO : " + to.getTemp());
		}
	}
}
