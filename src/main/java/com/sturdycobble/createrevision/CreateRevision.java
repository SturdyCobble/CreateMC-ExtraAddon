package com.sturdycobble.createrevision;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModItems;
import com.sturdycobble.createrevision.init.ModRecipeTypes;
import com.sturdycobble.createrevision.init.ModTags;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreateRevision.MODID)
@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD)
public class CreateRevision {

	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "createrevision";

	public CreateRevision() {
		MixinBootstrap.init();
		Mixins.addConfiguration("assets/createrevision/createrevision.mixins.json");
		ModConfigs.register();

		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModTags.register();
		modEventBus.addGenericListener(IRecipeSerializer.class, ModRecipeTypes::register);
		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
	}

}