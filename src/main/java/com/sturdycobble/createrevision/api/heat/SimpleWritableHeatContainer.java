package com.sturdycobble.createrevision.api.heat;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class SimpleWritableHeatContainer implements WritableHeatContainer, INBTSerializable<CompoundNBT> {

	private double temp = 300;

	@Override
	public double getTemp() {
		return temp;
	}

	@Override
	public double getCapacity() {
		return 1;
	}

	@Override
	public double getConductivity() {
		return 0.1;
	}

	@Override
	public void setTemp(double temp) {
		this.temp = temp;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("temp", getTemp());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setTemp(nbt.getDouble("temp"));
	}

}
