package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.heat.FrictionHeaterTileEntity;
import com.sturdycobble.createrevision.contents.heat.HeatEngineTileEntity;
import com.sturdycobble.createrevision.contents.heat.HeatExchangerTileEntity;
import com.sturdycobble.createrevision.contents.heat.HeatPipeTileEntity;
import com.sturdycobble.createrevision.contents.heat.ThermometerTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);
	
	public static final RegistryObject<TileEntityType<HeatPipeTileEntity>> HEAT_PIPE = TILE_ENTITY_TYPES
			.register("heat_pipe", () -> TileEntityType.Builder
			.create(HeatPipeTileEntity::new, ModBlocks.HEAT_PIPE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<FrictionHeaterTileEntity>> FRICTION_HEATER = TILE_ENTITY_TYPES
			.register("friction_heater", () -> TileEntityType.Builder
			.create(FrictionHeaterTileEntity::new, ModBlocks.FRICTION_HEATER.get()).build(null));
	
	public static final RegistryObject<TileEntityType<ThermometerTileEntity>> THERMOMETER = TILE_ENTITY_TYPES
			.register("thermometer", () -> TileEntityType.Builder
			.create(ThermometerTileEntity::new, ModBlocks.THERMOMETER.get()).build(null));

	public static final RegistryObject<TileEntityType<HeatExchangerTileEntity>> HEAT_EXCHANGER = TILE_ENTITY_TYPES
			.register("heat_exchanger", () -> TileEntityType.Builder
			.create(HeatExchangerTileEntity::new, ModBlocks.HEAT_EXCHANGER.get()).build(null));

	public static final RegistryObject<TileEntityType<HeatEngineTileEntity>> HEAT_ENGINE = TILE_ENTITY_TYPES
			.register("heat_engine", () -> TileEntityType.Builder
			.create(HeatEngineTileEntity::new, ModBlocks.HEAT_ENGINE.get()).build(null));
	
}