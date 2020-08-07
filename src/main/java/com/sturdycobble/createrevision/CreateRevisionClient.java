package com.sturdycobble.createrevision;

<<<<<<< HEAD
import com.simibubi.create.foundation.utility.SuperByteBufferCache;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
=======
import com.simibubi.create.foundation.ResourceReloadHandler;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
>>>>>>> 5cb49036360913cadc52633304987234009010f1
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateRevisionClient {

<<<<<<< HEAD
	public static SuperByteBufferCache bufferCache;

	public static void addClientListeners(IEventBus modEventBus) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(CreateRevisionClient::clientInit);
			modEventBus.addListener(CreateRevisionClient::onModelBake);
			modEventBus.addListener(CreateRevisionClient::onModelRegistry);
=======
	public static void addClientListeners(IEventBus modEventBus) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			modEventBus.addListener(CreateRevisionClient::clientInit);
>>>>>>> 5cb49036360913cadc52633304987234009010f1
		});
	}

	public static void clientInit(FMLClientSetupEvent event) {
<<<<<<< HEAD

		bufferCache = new SuperByteBufferCache();
		bufferCache.registerCompartment(ModBlockPartials.DIRECTIONAL_PARTIAL);

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

=======
		ModTileEntityTypes.registerRenderers();
		ModBlocks.blockRenderLayer();

		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager instanceof IReloadableResourceManager)
			((IReloadableResourceManager) resourceManager).addReloadListener(new ResourceReloadHandler());
	}
>>>>>>> 5cb49036360913cadc52633304987234009010f1
}
