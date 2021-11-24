package sturdycobble.createrevision.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ModItemGroups{

	public static class ModItemGroup extends ItemGroup{

		private final Supplier<ItemStack> iconSupplier;

		public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier){
			super(name);
			this.iconSupplier = iconSupplier;
		}

		@Override
		public ItemStack makeIcon(){
			return iconSupplier.get();
		}

	}

}