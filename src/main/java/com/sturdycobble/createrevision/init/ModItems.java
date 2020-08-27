package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CreateRevision.MODID);
	
	public static final RegistryObject<Item> BEDROCK_POWDER = ITEMS.register("bedrock_powder", () -> new Item(new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	
	public static final RegistryObject<BlockItem> GEOTHERMAL_FAN = ITEMS.register("geothermal_fan", () ->  new BlockItem(ModBlocks.GEOTHERMAL_FAN.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> OBSIDIAN_DRILL = ITEMS.register("obsidian_drill", () ->  new BlockItem(ModBlocks.OBSIDIAN_DRILL.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> BEDROCK_ANVIL = ITEMS.register("bedrock_anvil", () ->  new BlockItem(ModBlocks.BEDROCK_ANVIL.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> COMPACT_WOOL = ITEMS.register("compact_wool", () ->  new BlockItem(ModBlocks.COMPACT_WOOL.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> BEDROCK_SLATE = ITEMS.register("bedrock_slate", () ->  new BlockItem(ModBlocks.BEDROCK_SLATE.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> HEAT_PIPE = ITEMS.register("heat_pipe", () ->  new BlockItem(ModBlocks.HEAT_PIPE.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> THERMOMETER = ITEMS.register("thermometer", () ->  new BlockItem(ModBlocks.THERMOMETER.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> FRICTION_HEATER = ITEMS.register("friction_heater", () ->  new BlockItem(ModBlocks.FRICTION_HEATER.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	public static final RegistryObject<BlockItem> HEAT_EXCHANGER = ITEMS.register("heat_exchanger", () ->  new BlockItem(ModBlocks.HEAT_EXCHANGER.get(), new BlockItem.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
	
}
