package sturdycobble.createrevision.init;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import sturdycobble.createrevision.CreateRevision;

import java.util.function.Supplier;

public class ModItemGroups {

    public static final ModCreativeTab MOD_CREATE_TAB = new ModCreativeTab(
            CreateRevision.MODID, () -> new ItemStack(ModItems.REINFORCED_DEPOT.get()));

    public static class ModCreativeTab extends CreativeModeTab {

        private final Supplier<ItemStack> iconSupplier;

        public ModCreativeTab(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }

    }

}