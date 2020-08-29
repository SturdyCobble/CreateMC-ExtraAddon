package com.sturdycobble.createrevision;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.UnmodifiableIterator;
import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.utility.SuperByteBufferCache;
import com.sturdycobble.createrevision.contents.geo.bedrock.BedrockAnvilPressRenderer;
import com.sturdycobble.createrevision.contents.geo.bedrock.ObsidianDrillRenderer;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanRenderer;
import com.sturdycobble.createrevision.contents.heat.FrictionHeaterRenderer;
import com.sturdycobble.createrevision.contents.heat.HeatPipeModel;
import com.sturdycobble.createrevision.contents.heat.ThermometerRenderer;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class CreateRevisionClient {

	public static SuperByteBufferCache bufferCache;

	public static CustomBlockModels customBlockModels = new CustomBlockModels();

	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		bufferCache = new SuperByteBufferCache();
		bufferCache.registerCompartment(ModBlockPartials.DIRECTIONAL_PARTIAL);
		bufferCache.registerCompartment(ModBlockPartials.PARTIAL);

		ModBlocks.blockRenderLayer();

		for (Direction d : Direction.values())
			ModBlockPartials.HEAT_PIPE_RIMS.put(d, ModBlockPartials.getBlockPartial("heat_pipe/rim/" + d.getName()));
	}

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		ModBlockPartials.onModelBake(event);

		customBlockModels.foreach((block, modelFunc) 
				-> swapModels(modelRegistry, getAllBlockStateModelLocations(block), modelFunc));
	}

	private static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry, 
			List<ModelResourceLocation> locations, Function<IBakedModel, T> factory) {
		locations.forEach(location -> {
			swapModels(modelRegistry, location, factory);
		});
	}

	private static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry, 
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
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.THERMOMETER.get(), ThermometerRenderer::new);
	}

	@SubscribeEvent
	public static void registerBlockModels(ModelRegistryEvent event) {
		customBlockModels.register(ModBlocks.HEAT_PIPE.get().delegate, HeatPipeModel::new);
	}

	private static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block) {
		List<ModelResourceLocation> models = new ArrayList<>();
		UnmodifiableIterator<BlockState> iterator = block.getStateContainer().getValidStates().iterator();
		while (iterator.hasNext())
			models.add(new ModelResourceLocation(block.getRegistryName(), 
					BlockModelShapes.getPropertyMapString(iterator.next().getValues())));
		return models;
	}

}
