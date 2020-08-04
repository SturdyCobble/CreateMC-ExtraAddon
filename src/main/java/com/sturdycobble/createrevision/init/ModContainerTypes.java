package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * Container Registration
 * 
 * @author SturdyCobble
 *
 */
public class ModContainerTypes {
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, CreateRevision.MODID);

	// public static final RegistryObject<ContainerType<GeoTransformerContainer>> GEO_TRANSFORMER = CONTAINER_TYPES.register("geo_transformer_container", () -> IForgeContainerType.create(GeoTransformerContainer::new));
}
