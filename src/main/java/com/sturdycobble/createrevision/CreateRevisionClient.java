package com.sturdycobble.createrevision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.utility.SuperByteBufferCache;
import com.sturdycobble.createrevision.contents.geo.bedrock.BedrockAnvilPressRenderer;
import com.sturdycobble.createrevision.contents.geo.bedrock.ObsidianDrillRenderer;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanRenderer;
import com.sturdycobble.createrevision.contents.heat.source.FrictionHeaterRenderer;
import com.sturdycobble.createrevision.contents.heat.transfer.HeatPipeModel;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
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

	public static CustomBlockModels customBlockModels;
	
	static {
		customBlockModels = new CustomBlockModels();
	}
	
	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		bufferCache = new SuperByteBufferCache();
		bufferCache.registerCompartment(ModBlockPartials.DIRECTIONAL_PARTIAL);
		bufferCache.registerCompartment(ModBlockPartials.PARTIAL);

		ModBlocks.blockRenderLayer();
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		ModBlockPartials.onModelBake(event);
		
		customBlockModels
		.foreach((block, modelFunc) -> swapModels(modelRegistry, getAllBlockStateModelLocations(block), modelFunc));
	}
	
	public static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry,
			List<ModelResourceLocation> locations, Function<IBakedModel, T> factory) {
			locations.forEach(location -> {
				swapModels(modelRegistry, location, factory);
			});
		}
		
	public static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry,
		ModelResourceLocation location, Function<IBakedModel, T> factory) {
		modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
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
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FRICTION_HEATER.get(), FrictionHeaterRenderer::new);
	}
	
	@SubscribeEvent
	public static void registerBlockModels(ModelRegistryEvent event) {
		CreateRevisionClient.customBlockModels
		.register(ModBlocks.HEAT_PIPE.get().delegate, HeatPipeModel::new);
	}
	
	public static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block) {
		List<ModelResourceLocation> models = new ArrayList<>();
		block.getStateContainer()
			.getValidStates()
			.forEach(state -> {
				models.add(new ModelResourceLocation(block.getRegistryName(), BlockModelShapes.getPropertyMapString(state.getValues())));
			});
		return models;
	}

}
