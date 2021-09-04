package sturdycobble.createrevision.init;

import java.util.function.Supplier;

import sturdycobble.createrevision.CreateRevision;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroups {

    public static final ItemGroup MOD_ITEM_GROUP = new ModItemGroup(CreateRevision.MODID, () -> new ItemStack(ModItems.HEAT_PIPE.get()));

    public static class ModItemGroup extends ItemGroup {

        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }

    }

}