package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * TileEntity Type Registration
 * 
 * @author SturdyCobble
 *
 */
public class ModTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);

	public static final RegistryObject<TileEntityType<GeothermalFanTileEntity>> GEOTHERMAL_FAN = 
			TILE_ENTITY_TYPES.register("geothermal_fan", () -> TileEntityType.Builder.create(GeothermalFanTileEntity::new, 
					ModBlocks.GEOTHERMAL_FAN.get()).build(null));
	/*public static final RegistryObject<TileEntityType<GeoTransformerTileEntity>> GEO_TRANSFORMER = 
			TILE_ENTITY_TYPES.register("geo_transformer", () -> TileEntityType.Builder.create(GeoTransformerTileEntity::new, 
					ModBlocks.GEO_TRANSFORMER.get()).build(null));*/
}