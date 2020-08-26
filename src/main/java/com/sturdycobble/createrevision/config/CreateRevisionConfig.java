package com.sturdycobble.createrevision.config;

import java.nio.file.Path;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public class CreateRevisionConfig  {

	public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ConfigValues COMMON;

	static {
		Function <ForgeConfigSpec.Builder, ConfigValues> consumer = (builder) -> (new ConfigValues(builder));
		final Pair<ConfigValues, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(consumer);
		COMMON = specPair.getLeft();
		COMMON_CONFIG = specPair.getRight();
	}
	
	public static void register() {
		
		ModLoadingContext.get()
		.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
		
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
	            .sync()
	            .preserveInsertionOrder()
	            .autosave()
	            .writingMode(WritingMode.REPLACE)
	            .build();

	    	configData.load();
	    	spec.setConfig(configData);
	}
	
}
