package com.sturdycobble.createrevision.api.heat;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface HeatContainer extends INBTSerializable<CompoundNBT> {

	public abstract double getTemp();

	public abstract void setTemp(double tempIn);

	public abstract double getCapacity();
	
	@Override
	default CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("temp", getTemp());
		return nbt;
	}

	@Override
	default void deserializeNBT(CompoundNBT nbt) {
		setTemp(nbt.getDouble("temp"));
	}

}
