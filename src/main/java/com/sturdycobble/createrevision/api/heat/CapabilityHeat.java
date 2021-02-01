package com.sturdycobble.createrevision.api.heat;

import javax.annotation.Nullable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityHeat {

	@CapabilityInject(HeatContainer.class)
	public static Capability<HeatContainer> HEAT_CAPABILITY = null;
	
	public static void register() {
		CapabilityManager.INSTANCE.register(HeatContainer.class, new Capability.IStorage<HeatContainer>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<HeatContainer> capability, HeatContainer instance, Direction side) {
				return null;
			}	
			
			@Override
			public void readNBT(Capability<HeatContainer> capability, HeatContainer instance, Direction side, INBT nbt) { }
		}, () -> { throw new UnsupportedOperationException(); });
	}
	
}