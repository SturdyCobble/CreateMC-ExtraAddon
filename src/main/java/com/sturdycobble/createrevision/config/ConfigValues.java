package com.sturdycobble.createrevision.config;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ConfigValues {

	public final ForgeConfigSpec.IntValue friction_heater_stress_applied;
	
	public ConfigValues(ForgeConfigSpec.Builder SERVER_BUILDER) {
		SERVER_BUILDER.comment("Stress Configuration").push("stress_config");
		
		friction_heater_stress_applied = SERVER_BUILDER.comment("Stress Applied For Friction Heater (Default = 16 / RPM)")
				.translation(CreateRevision.MODID+".config.friction_heater_stress_applied")
				.defineInRange("friction_heater_stress_applied", 16, 0, 1024);

		SERVER_BUILDER.pop();
	}
	
}