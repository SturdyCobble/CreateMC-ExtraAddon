package sturdycobble.createrevision;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sturdycobble.createrevision.datagen.BlockTagGen;
import sturdycobble.createrevision.datagen.FluidTagGen;
import sturdycobble.createrevision.init.*;

@Mod(CreateRevision.MODID)
@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD)
public class CreateRevision {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "createrevision";

    public CreateRevision() {
        ModConfigs.register();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(RecipeSerializer.class, ModRecipeTypes::register);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeServer()) {
            gen.addProvider(new BlockTagGen(gen, event.getExistingFileHelper()));
            gen.addProvider(new FluidTagGen(gen, event.getExistingFileHelper()));
        }
    }

}