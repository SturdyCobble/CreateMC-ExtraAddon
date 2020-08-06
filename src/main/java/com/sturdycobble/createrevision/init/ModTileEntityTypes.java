package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanRenderer;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);

	public static final RegistryObject<TileEntityType<GeothermalFanTileEntity>> GEOTHERMAL_FAN = TILE_ENTITY_TYPES
			.register("geothermal_fan", () -> TileEntityType.Builder
			.create(GeothermalFanTileEntity::new, ModBlocks.GEOTHERMAL_FAN.get()).build(null));

	@OnlyIn(Dist.CLIENT)
	public static void registerRenderers() {
		ClientRegistry.bindTileEntityRenderer(GEOTHERMAL_FAN.get(), GeothermalFanRenderer::new);
	}
}