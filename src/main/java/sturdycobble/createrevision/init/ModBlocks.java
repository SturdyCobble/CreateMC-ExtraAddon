package sturdycobble.createrevision.init;

import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.heat.FrictionHeaterBlock;
import sturdycobble.createrevision.contents.heat.HeatEngineBlock;
import sturdycobble.createrevision.contents.heat.HeatExchangerBlock;
import sturdycobble.createrevision.contents.heat.HeatPipeBlock;
import sturdycobble.createrevision.contents.heat.ThermometerBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreateRevision.MODID);

    public static final RegistryObject<Block> HEAT_PIPE = BLOCKS.register("heat_pipe",
            () -> new HeatPipeBlock(Block.Properties.of(Material.HEAVY_METAL).strength(6F, 6F)));
    public static final RegistryObject<Block> THERMOMETER = BLOCKS.register("thermometer",
            () -> new ThermometerBlock(Block.Properties.of(Material.HEAVY_METAL).strength(0.5F, 0.5F)));
    public static final RegistryObject<Block> FRICTION_HEATER = BLOCKS.register("friction_heater",
            () -> new FrictionHeaterBlock(Block.Properties.of(Material.HEAVY_METAL).strength(1.0F, 1.0F)));
    public static final RegistryObject<Block> HEAT_EXCHANGER = BLOCKS.register("heat_exchanger",
            () -> new HeatExchangerBlock(Block.Properties.of(Material.HEAVY_METAL).strength(6F, 6F)));
    public static final RegistryObject<Block> HEAT_ENGINE = BLOCKS.register("heat_engine",
            () -> new HeatEngineBlock(Block.Properties.of(Material.HEAVY_METAL).strength(1F, 3F)));

}