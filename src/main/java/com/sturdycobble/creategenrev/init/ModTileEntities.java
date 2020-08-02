package com.sturdycobble.creategenrev.init;

import com.sturdycobble.creategenrev.CreateGeneratorRevision;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
public class ModTileEntities {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, CreateGeneratorRevision.MODID);

}