package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import net.minecraft.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Item Registration
 *
 * @author SturdyCobble
 */

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CreateRevision.MODID);
}
