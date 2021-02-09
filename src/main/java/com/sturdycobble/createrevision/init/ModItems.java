package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.heat.IRGogglesItem;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CreateRevision.MODID);

	public static final RegistryObject<IRGogglesItem> IR_GOGGLES = ITEMS.register("ir_goggles", () -> new IRGogglesItem(new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));

	public static final RegistryObject<BlockItem> HEAT_PIPE = ITEMS.register("heat_pipe", () -> new BlockItem(ModBlocks.HEAT_PIPE.get(), new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> THERMOMETER = ITEMS.register("thermometer", () -> new BlockItem(ModBlocks.THERMOMETER.get(), new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> FRICTION_HEATER = ITEMS.register("friction_heater", () -> new BlockItem(ModBlocks.FRICTION_HEATER.get(), new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> HEAT_EXCHANGER = ITEMS.register("heat_exchanger", () -> new BlockItem(ModBlocks.HEAT_EXCHANGER.get(), new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> HEAT_ENGINE = ITEMS.register("heat_engine", () -> new BlockItem(ModBlocks.HEAT_ENGINE.get(), new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	
}
