package com.sturdycobble.createrevision.contents.heatsystem;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityHeat {
    @CapabilityInject(HeatContainer.class)
    public static Capability<HeatContainer> HEAT_CAPABILITY = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(HeatContainer.class, new Capability.IStorage<HeatContainer>()
        {
            @Nullable
            @Override
            public INBT writeNBT(Capability<HeatContainer> capability, HeatContainer instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<HeatContainer> capability, HeatContainer instance, Direction side, INBT nbt) {

            }
        },()->new SimpleHeatContainer(30,300,10));
    }
}
