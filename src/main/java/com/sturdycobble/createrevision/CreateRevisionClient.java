package com.sturdycobble.createrevision;

import com.simibubi.create.foundation.utility.SuperByteBufferCache;
import com.sturdycobble.createrevision.contents.geo.bedrock.BedrockAnvilPressRenderer;
import com.sturdycobble.createrevision.contents.geo.bedrock.ObsidianDrillRenderer;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanRenderer;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD,  value = Dist.CLIENT)
public class CreateRevisionClient {

	public static SuperByteBufferCache bufferCache;

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		bufferCache = new SuperByteBufferCache();
		bufferCache.registerCompartment(ModBlockPartials.DIRECTIONAL_PARTIAL);
		bufferCache.registerCompartment(ModBlockPartials.PARTIAL);

		ModBlocks.blockRenderLayer();
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		ModBlockPartials.onModelBake(event);
	}

	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event) {
		ModBlockPartials.onModelRegistry(event);
	}
	
	@SubscribeEvent
	public static void registerRenderers(ModelRegistryEvent event) {
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.GEOTHERMAL_FAN.get(), GeothermalFanRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.OBSIDIAN_DRILL.get(), ObsidianDrillRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BEDROCK_ANVIL.get(), BedrockAnvilPressRenderer::new);
	}

}
