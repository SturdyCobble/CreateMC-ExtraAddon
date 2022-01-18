package sturdycobble.createrevision;

import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class CreateRevisionClient{

	public static SuperByteBufferCache bufferCache;

	public static CustomBlockModels customBlockModels;

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event){
		Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();

	}

	private static <T extends BakedModel> void swapModels(Map<ResourceLocation, BakedModel> modelRegistry,
	                                                       List<ModelResourceLocation> locations, Function<BakedModel, T> factory){
		locations.forEach(location -> {
			swapModels(modelRegistry, location, factory);
		});
	}

	private static <T extends BakedModel> void swapModels(Map<ResourceLocation, BakedModel> modelRegistry,
	                                                       ModelResourceLocation location, Function<BakedModel, T> factory){
		modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
	}

	@SubscribeEvent
	public static void registerRenderers(ModelRegistryEvent event){
	}

	@SubscribeEvent
	public static void registerBlockModels(ModelRegistryEvent event){
	}

	public static CustomBlockModels getCustomBlockModels(){
		if(customBlockModels==null)
			customBlockModels = new CustomBlockModels();
		return customBlockModels;
	}

	protected static List<ModelResourceLocation> getAllBlockStateModelLocations(Block block){
		List<ModelResourceLocation> models = new ArrayList<>();
		block.getStateDefinition()
				.getPossibleStates()
				.forEach(state -> {
					models.add(new ModelResourceLocation(block.getRegistryName(), BlockModelShaper.statePropertiesToString(state.getValues())));
				});
		return models;
	}

}
