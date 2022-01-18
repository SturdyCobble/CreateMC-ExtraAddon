package sturdycobble.createrevision.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sturdycobble.createrevision.CreateRevision;

public class ModTileEntityTypes{

	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(
			ForgeRegistries.BLOCK_ENTITIES, CreateRevision.MODID);
}