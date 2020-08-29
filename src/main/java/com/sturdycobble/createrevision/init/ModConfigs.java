package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModConfigs  {
	
	public static DoubleValue frictionHeaterStress;
	
	public static void register() {
		ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
		
		common.comment("Stress Configuration").push("stress_config");
		frictionHeaterStress = common.comment("Stress Applied For Friction Heater (Default = 16 / RPM, 0 < x < 1024)")
				.translation(CreateRevision.MODID+".config.friction_heater_stress_applied")
				.defineInRange("friction_heater_stress_applied", 16D, 0D, 1024D);
		common.pop();
		
		ModLoadingContext.get()
		.registerConfig(ModConfig.Type.COMMON, common.build());
	}

}