package sturdycobble.createrevision.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sturdycobble.createrevision.CreateRevision;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreateRevision.MODID);

    public static final RegistryObject<Item> DEEPSLATE_ALLOY = ITEMS.register("deepslate_alloy", () -> new Item(new Item.Properties().tab(ModItemGroups.MOD_CREATE_TAB)));
    public static final RegistryObject<Item> DEEPSLATE_ALLOY_SHEET = ITEMS.register("deepslate_alloy_sheet", () -> new Item(new Item.Properties().tab(ModItemGroups.MOD_CREATE_TAB)));
    public static final RegistryObject<Item> GLITTERING_AMETHYST_SHARD = ITEMS.register("glittering_amethyst_shard", () -> new Item(new Item.Properties().tab(ModItemGroups.MOD_CREATE_TAB)));

    public static final RegistryObject<BlockItem> REINFORCED_DEPOT = ITEMS.register("reinforced_depot",
            () -> new BlockItem(ModBlocks.REINFORCED_DEPOT.get(), new BlockItem.Properties().tab(ModItemGroups.MOD_CREATE_TAB)));

}
