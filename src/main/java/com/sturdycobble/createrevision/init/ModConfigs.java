package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ModConfigs {

	private static DoubleValue frictionHeaterStress;

	private static DoubleValue heatPipeConductivity;
	private static DoubleValue heatPipeHeatCapacity;
	private static DoubleValue frictionHeaterHeatCapacity;
	private static DoubleValue frictionHeaterConductivity;
	private static DoubleValue kindledBlazeBurnerPower;
	private static DoubleValue seethingBlazeBurnerPower;
	private static DoubleValue heatEngineEfficiency;
	private static DoubleValue kindledBlazeBurnerMaxTemp;
	private static DoubleValue seethingBlazeBurnerMaxTemp;

	public static float getFrictionHeaterStress() {
		return frictionHeaterStress.get().floatValue();
	}

	public static double getHeatPipeConductivity() {
		return heatPipeConductivity.get();
	}

	public static double getHeatPipeHeatCapacity() {
		return heatPipeHeatCapacity.get();
	}

	public static double getFrictionHeaterHeatCapacity() {
		return frictionHeaterHeatCapacity.get();
	}

	public static double getFrictionHeaterConductivity() {
		return frictionHeaterConductivity.get();
	}

	public static double getKindledBlazeBurnerPower() {
		return kindledBlazeBurnerPower.get();
	}

	public static double getSeethingBlazeBurnerPower() {
		return seethingBlazeBurnerPower.get();
	}

	public static double getHeatEngineEfficiency() {
		return heatEngineEfficiency.get();
	}

	public static double getKindledBlazeBurnerMaxTemp() {
		return kindledBlazeBurnerMaxTemp.get();
	}

	public static double getSeethingBlazeBurnerMaxTemp() {
		return seethingBlazeBurnerMaxTemp.get();
	}

	public static void register() {
		ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();

		common.comment("Stress Configuration").push("stress_config");

		frictionHeaterStress = common.comment("Stress Applied For Friction Heater (Default = 16 / RPM)")
				.translation(CreateRevision.MODID + ".config.friction_heater_stress_applied")
				.defineInRange("friction_heater_stress_applied", 16D, 0D, 1024D);

		common.pop();

		common.comment("Conductivity Configuration").push("conduct_config");

		heatPipeConductivity = common.comment("Conductivity For Heat Pipe (Default = 0.10)")
				.translation(CreateRevision.MODID + ".config.heat_pipe_conductivity")
				.defineInRange("heat_pipe_conductivity", 0.10, 0.01, 0.166);
		frictionHeaterConductivity = common.comment("Conductivity For Friction Heater (Default = 0.05)")
				.translation(CreateRevision.MODID + ".config.friction_heater_conductivity")
				.defineInRange("friction_heater_conductivity", 0.05, 0.01, 0.166);

		common.pop();

		common.comment("Heat Capacity Configuration").push("heat_capacity_config");

		heatPipeHeatCapacity = common.comment("Heat Capacity For Heat Pipe (Default = 1)")
				.translation(CreateRevision.MODID + ".config.heat_pipe_heat_capacity")
				.defineInRange("heat_pipe_heat_capacity", 1D, 1D, 100D);

		frictionHeaterHeatCapacity = common.comment("Heat Capacity For Friction Heater (Default = 3)")
				.translation(CreateRevision.MODID + ".config.friction_heater_heat_capacity")
				.defineInRange("friction_heater_heat_capacity", 3D, 2D, 100D);

		common.pop();

		common.comment("Power Configuration").push("heat_power_config");

		kindledBlazeBurnerPower = common.comment("Power of the Kindled Blaze Burner (Default = 6)")
				.translation(CreateRevision.MODID + ".config.kindled_blaze_burner_power")
				.defineInRange("kindled_blaze_burner_power", 6D, 0D, 100D);
		seethingBlazeBurnerPower = common.comment("Power of the Kindled Seething Burner (Default = 15)")
				.translation(CreateRevision.MODID + ".config.seething_blaze_burner_power")
				.defineInRange("seething_blaze_burner_power", 15D, 0D, 100D);
		heatEngineEfficiency = common.comment("Efficienct Factor of Heat Engine (Default = 4)")
				.translation(CreateRevision.MODID + ".config.heat_engine_efficiency")
				.defineInRange("heat_engine_efficiency", 4D, 0D, 256D);
		kindledBlazeBurnerMaxTemp = common.comment("Maximum Temperature for the Kindled Blaze Burner (Default = 600)")
				.translation(CreateRevision.MODID + ".config.kindled_blaze_burner_max_temp")
				.defineInRange("kindled_blaze_burner_max_temp", 600D, 0D, 5000D);
		seethingBlazeBurnerMaxTemp = common
				.comment("Maximum Temperature for the the Kindled Seething Burner (Default = 1100)")
				.translation(CreateRevision.MODID + ".config.seething_blaze_burner_max_temp")
				.defineInRange("seething_blaze_burner_max_temp", 1100D, 0D, 5000D);

		common.pop();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.build());
	}

}