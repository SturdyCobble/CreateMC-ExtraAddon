package sturdycobble.createrevision.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import sturdycobble.createrevision.CreateRevision;

public class ModConfigs {

    private static ForgeConfigSpec.IntValue beaconDepotProcessingTime;
    private static ForgeConfigSpec.DoubleValue highChroma;
    private static ForgeConfigSpec.DoubleValue lowChroma;
    private static ForgeConfigSpec.DoubleValue highValue;
    private static ForgeConfigSpec.DoubleValue lowValue;
    private static ForgeConfigSpec.DoubleValue resolution;


    public static int getBeaconDepotProcessingTime() {
        return beaconDepotProcessingTime.get();
    }

    public static float getHighChromaThreshold() {
        return highChroma.get().floatValue();
    }

    public static float getLowChromaThreshold() {
        return lowChroma.get().floatValue();
    }

    public static float getHighValueThreshold() {
        return highValue.get().floatValue();
    }

    public static float getLowValueThreshold() {
        return lowValue.get().floatValue();
    }

    public static float getMinimumDistinguishableRGBDistanceSquared() {
        return resolution.get().floatValue();
    }

    public static void register() {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.comment("Processing Configuration").push("processing_config");

        beaconDepotProcessingTime = common.comment("Processing Time for Beacon Depot Recipe (Default = 8)")
                .translation(CreateRevision.MODID + ".config.beacon_depot_process_time")
                .defineInRange("beacon_depot_process_time", 8, 1, 1000000);

        common.pop();

        common.comment("Color Related Configuration").push("color_config");

        highChroma = common.comment("Define threshold for high chroma condition (Default = 0.5)")
                .translation(CreateRevision.MODID + ".config.high_chroma")
                .defineInRange("high_chroma", 0.5, 0.0, 1.0);
        lowChroma = common.comment("Define upper bound for low chroma condition (Default = 0.2)")
                .translation(CreateRevision.MODID + ".config.low_chroma")
                .defineInRange("low_chroma", 0.2, 0.0, 1.0);
        highValue = common.comment("Define threshold for high brightness condition (Default = 0.7)")
                .translation(CreateRevision.MODID + ".config.high_value")
                .defineInRange("high_value", 0.7, 0.0, 1.0);
        lowValue = common.comment("Define upper bound for low brightness condition (Default = 0.3)")
                .translation(CreateRevision.MODID + ".config.low_value")
                .defineInRange("high_value", 0.3, 0.0, 1.0);
        resolution = common.comment("Define the minimum distance square value in RGB of two distinguished colors. Each component of a RGB color is within [0,1]. (Default = 0.01)")
                .translation(CreateRevision.MODID + ".config.color_resolution")
                .defineInRange("color_resolution", 0.01, 0.0, 1.0);

        common.pop();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.build());
    }

}