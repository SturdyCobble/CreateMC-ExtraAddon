package sturdycobble.createrevision.init;

import sturdycobble.createrevision.CreateRevision;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(
            ForgeRegistries.CONTAINERS, CreateRevision.MODID);

}
