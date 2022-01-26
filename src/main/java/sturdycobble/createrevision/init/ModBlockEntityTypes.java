package sturdycobble.createrevision.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.reinforced_depot.ReinforcedDepotBlockEntity;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.BLOCK_ENTITIES, CreateRevision.MODID);

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(BlockEntityType.BlockEntitySupplier<T> tile, Block validBlock) {
        return BlockEntityType.Builder.of(tile, validBlock).build(null);
    }    public static RegistryObject<BlockEntityType<ReinforcedDepotBlockEntity>> REINFORCED_DEPOT = BLOCK_ENTITY_TYPES
            .register("reinforced_depot", () -> registerBlockEntity(ReinforcedDepotBlockEntity::new, ModBlocks.REINFORCED_DEPOT.get()));



}