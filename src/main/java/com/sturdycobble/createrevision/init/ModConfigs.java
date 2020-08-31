package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModConfigs  {
	
	public static DoubleValue frictionHeaterStress;
	
	public static DoubleValue heatPipeConductivity;
	public static DoubleValue heatPipeHeatCapacity;
	public static DoubleValue frictionHeaterHeatCapacity;
	public static DoubleValue frictionHeaterConductivity;
	
	public static void register() {
		ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
		
		common.comment("Stress Configuration").push("stress_config");
		
		frictionHeaterStress = common.comment("Stress Applied For Friction Heater (Default = 16 / RPM)")
				.translation(CreateRevision.MODID+".config.friction_heater_stress_applied")
				.defineInRange("friction_heater_stress_applied", 16D, 0D, 1024D);
		
		common.pop();
		
		common.comment("Conductivity Configuration").push("conduct_config");
		
		heatPipeConductivity = common.comment("Conductivity For Heat Pipe (Default = 0.10)")
				.translation(CreateRevision.MODID+".config.heat_pipe_conductivity")
				.defineInRange("heat_pipe_conductivity", 0.10, 0.01, 0.166);
		frictionHeaterConductivity = common.comment("Conductivity For Friction Heater (Default = 0.05)")
				.translation(CreateRevision.MODID+".config.friction_heater_conductivity")
				.defineInRange("friction_heater_conductivity", 0.05, 0.01, 0.166);
		
		common.pop();
		
		common.comment("Heat Capacity Configuration").push("heat_capacity_config");
		
		heatPipeHeatCapacity = common.comment("Heat Capacity For Heat Pipe (Default = 1)")
				.translation(CreateRevision.MODID+".config.heat_pipe_heat_capacity")
				.defineInRange("heat_pipe_heat_capacity", 1D, 1D, 100D);
		
		frictionHeaterHeatCapacity = common.comment("Heat Capacity For Friction Heater (Default = 3)")
				.translation(CreateRevision.MODID+".config.friction_heater_heat_capacity")
				.defineInRange("friction_heater_heat_capacity", 3D, 2D, 100D);
		
		common.pop();
		
		ModLoadingContext.get()
		.registerConfig(ModConfig.Type.COMMON, common.build());
	}

}