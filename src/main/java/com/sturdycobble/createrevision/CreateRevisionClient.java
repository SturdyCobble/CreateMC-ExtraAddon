package com.sturdycobble.createrevision;

import com.simibubi.create.foundation.utility.SuperByteBufferCache;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateRevisionClient {

	public static SuperByteBufferCache bufferCache;

	public static void addClientListeners(IEventBus modEventBus) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(CreateRevisionClient::clientInit);
			modEventBus.addListener(CreateRevisionClient::onModelBake);
			modEventBus.addListener(CreateRevisionClient::onModelRegistry);
		});
	}

	public static void clientInit(FMLClientSetupEvent event) {

		bufferCache = new SuperByteBufferCache();
		bufferCache.registerCompartment(ModBlockPartials.DIRECTIONAL_PARTIAL);
		bufferCache.registerCompartment(ModBlockPartials.PARTIAL);

		ModTileEntityTypes.registerRenderers();
		ModBlocks.blockRenderLayer();
	}

	@OnlyIn(Dist.CLIENT)
	public static void onModelBake(ModelBakeEvent event) {
		ModBlockPartials.onModelBake(event);
	}

	@OnlyIn(Dist.CLIENT)
	public static void onModelRegistry(ModelRegistryEvent event) {
		ModBlockPartials.onModelRegistry(event);
	}

}
