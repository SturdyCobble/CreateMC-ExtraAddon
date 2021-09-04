package sturdycobble.createrevision.init;

import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.heat.FrictionHeaterTileEntity;
import sturdycobble.createrevision.contents.heat.HeatEngineTileEntity;
import sturdycobble.createrevision.contents.heat.HeatExchangerTileEntity;
import sturdycobble.createrevision.contents.heat.HeatPipeTileEntity;
import sturdycobble.createrevision.contents.heat.ThermometerTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.TILE_ENTITIES, CreateRevision.MODID);

    public static final RegistryObject<TileEntityType<HeatPipeTileEntity>> HEAT_PIPE = TILE_ENTITY_TYPES
            .register("heat_pipe", () -> TileEntityType.Builder
                    .of(HeatPipeTileEntity::new, ModBlocks.HEAT_PIPE.get()).build(null));

    public static final RegistryObject<TileEntityType<FrictionHeaterTileEntity>> FRICTION_HEATER = TILE_ENTITY_TYPES
            .register("friction_heater", () -> TileEntityType.Builder
                    .of(FrictionHeaterTileEntity::new, ModBlocks.FRICTION_HEATER.get()).build(null));

    public static final RegistryObject<TileEntityType<ThermometerTileEntity>> THERMOMETER = TILE_ENTITY_TYPES
            .register("thermometer", () -> TileEntityType.Builder
                    .of(ThermometerTileEntity::new, ModBlocks.THERMOMETER.get()).build(null));

    public static final RegistryObject<TileEntityType<HeatExchangerTileEntity>> HEAT_EXCHANGER = TILE_ENTITY_TYPES
            .register("heat_exchanger", () -> TileEntityType.Builder
                    .of(HeatExchangerTileEntity::new, ModBlocks.HEAT_EXCHANGER.get()).build(null));

    public static final RegistryObject<TileEntityType<HeatEngineTileEntity>> HEAT_ENGINE = TILE_ENTITY_TYPES
            .register("heat_engine", () -> TileEntityType.Builder
                    .of(HeatEngineTileEntity::new, ModBlocks.HEAT_ENGINE.get()).build(null));

}