package sturdycobble.createrevision;

import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
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
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();

	}

	private static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry,
	                                                       List<ModelResourceLocation> locations, Function<IBakedModel, T> factory){
		locations.forEach(location -> {
			swapModels(modelRegistry, location, factory);
		});
	}

	private static <T extends IBakedModel> void swapModels(Map<ResourceLocation, IBakedModel> modelRegistry,
	                                                       ModelResourceLocation location, Function<IBakedModel, T> factory){
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
					models.add(new ModelResourceLocation(block.getRegistryName(), BlockModelShapes.statePropertiesToString(state.getValues())));
				});
		return models;
	}

}
