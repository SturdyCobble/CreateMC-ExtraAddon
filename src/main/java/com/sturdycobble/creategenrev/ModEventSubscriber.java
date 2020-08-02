package com.sturdycobble.creategenrev;

import com.sturdycobble.creategenrev.init.ModBlocks;
import com.sturdycobble.creategenrev.init.ModItemGroups;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = CreateGeneratorRevision.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEventSubscriber{
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)
		.forEach(block -> {
			final BlockItem blockItem = new BlockItem(block, new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP));
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}
	
	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		/* final IForgeRegistryModifiable<Block> modRegistry = (IForgeRegistryModifiable<Block>) event.getRegistry();
		ResourceLocation bearing = new ResourceLocation("create:mechanical_bearing");
		modRegistry.remove(bearing);
		ResourceLocation water = new ResourceLocation("create:water_wheel");
		modRegistry.remove(water);
		ResourceLocation furnace = new ResourceLocation("furnace_engine");
		modRegistry.remove(furnace); */
	}

}
