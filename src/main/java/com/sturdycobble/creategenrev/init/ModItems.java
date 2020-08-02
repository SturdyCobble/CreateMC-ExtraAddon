package com.sturdycobble.creategenrev.init;

import com.sturdycobble.creategenrev.CreateGeneratorRevision;

import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * Item Registration.
 * 
 * @author StdCobble
 *
 */

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CreateGeneratorRevision.MODID);
}
