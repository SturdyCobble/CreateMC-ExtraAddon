package com.sturdycobble.creategenrev.init;

import java.util.function.Supplier;

import com.sturdycobble.creategenrev.CreateGeneratorRevision;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
/**
 * Main ItemGroup for the Mod.
 * 
 * @author StdCobble
 *
 */
public class ModItemGroups{
	public static final ItemGroup MOD_ITEM_GROUP = new ModItemGroup(CreateGeneratorRevision.MODID, () -> new ItemStack(Items.LIGHT_BLUE_BANNER));
	public static class ModItemGroup extends ItemGroup{
		private final Supplier<ItemStack> iconSupplier;
		public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
			super(name);
			this.iconSupplier = iconSupplier;
		}
		
		@Override
		public ItemStack createIcon() {
			return iconSupplier.get();
		}
	}
}