package sturdycobble.createrevision.init;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.reinforced_depot.ReinforcedDepotBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateRevision.MODID);

    public static final RegistryObject<Block> REINFORCED_DEPOT = BLOCKS.register("reinforced_depot",
            () -> new ReinforcedDepotBlock(Block.Properties.of(Material.HEAVY_METAL).strength(3F, 1200F)));

    public static final void blockRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.REINFORCED_DEPOT.get(), RenderType.cutoutMipped());
    }

}