package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			CreateRevision.MODID);
	public static final RegistryObject<Item> BEDROCK_POWDER = ITEMS.register("bedrock_powder", () -> new Item(new Item.Properties().group(ModItemGroups.MOD_ITEM_GROUP)));
}
