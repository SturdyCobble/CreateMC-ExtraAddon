package sturdycobble.createrevision.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sturdycobble.createrevision.CreateRevision;

public class ModTileEntityTypes{

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(
			ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);
}