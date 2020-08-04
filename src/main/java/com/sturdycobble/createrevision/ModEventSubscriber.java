package com.sturdycobble.createrevision;

import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModItemGroups;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;
/**
 * EventSubscribers.
 * @author SturdyCobble
 *
 */
@EventBusSubscriber(modid = CreateRevision.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEventSubscriber{
	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		
		//Register Block Items.
		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)
		.forEach(block -> {
			final BlockItem blockItem = new BlockItem(block, new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP));
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}
	/*
	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {

	}*/

}
