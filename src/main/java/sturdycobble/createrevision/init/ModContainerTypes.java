package sturdycobble.createrevision.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sturdycobble.createrevision.CreateRevision;

public class ModContainerTypes {

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(
            ForgeRegistries.CONTAINERS, CreateRevision.MODID);

}
