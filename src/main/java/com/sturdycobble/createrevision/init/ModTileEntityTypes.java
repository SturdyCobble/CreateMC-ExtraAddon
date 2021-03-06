package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.geo.bedrock.BedrockAnvilTileEntity;
import com.sturdycobble.createrevision.contents.geo.bedrock.ObsidianDrillTileEntity;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);

	public static final RegistryObject<TileEntityType<GeothermalFanTileEntity>> GEOTHERMAL_FAN = TILE_ENTITY_TYPES
			.register("geothermal_fan", () -> TileEntityType.Builder
			.create(GeothermalFanTileEntity::new, ModBlocks.GEOTHERMAL_FAN.get()).build(null));
	
	public static final RegistryObject<TileEntityType<ObsidianDrillTileEntity>> OBSIDIAN_DRILL = TILE_ENTITY_TYPES
			.register("obsidian_drill", () -> TileEntityType.Builder
			.create(ObsidianDrillTileEntity::new, ModBlocks.OBSIDIAN_DRILL.get()).build(null));
	
	public static final RegistryObject<TileEntityType<BedrockAnvilTileEntity>> BEDROCK_ANVIL = TILE_ENTITY_TYPES
			.register("bedrock_anvil", () -> TileEntityType.Builder
			.create(BedrockAnvilTileEntity::new, ModBlocks.BEDROCK_ANVIL.get()).build(null));
	
}