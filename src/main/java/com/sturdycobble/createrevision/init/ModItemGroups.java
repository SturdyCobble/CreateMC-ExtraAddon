package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.function.Supplier;

/**
 * ItemGroup Registration
 *
 * @author SturdyCobble
 */
public class ModItemGroups {
	public static final ItemGroup MOD_ITEM_GROUP = new ModItemGroup(CreateRevision.MODID, () -> new ItemStack(Items.DAYLIGHT_DETECTOR));

	public static class ModItemGroup extends ItemGroup {
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