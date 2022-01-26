package sturdycobble.createrevision.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import sturdycobble.createrevision.CreateRevision;

public class ModConfigs {

    private static ForgeConfigSpec.IntValue beaconDepotProcessingTime;

    public static int getBeaconDepotProcessingTime() {
        return beaconDepotProcessingTime.get();
    }

    public static void register() {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.comment("Processing Configuration").push("processing_config");

        beaconDepotProcessingTime = common.comment("Processing Time for Beacon Depot Recipe (Default = 8)")
                .translation(CreateRevision.MODID + ".config.beacon_depot_process_time")
                .defineInRange("beacon depot process time", 8, 1, 1000000);

        common.pop();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, common.build());
    }

}