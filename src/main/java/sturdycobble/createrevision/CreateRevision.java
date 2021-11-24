package sturdycobble.createrevision;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sturdycobble.createrevision.init.ModBlocks;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.init.ModItems;
import sturdycobble.createrevision.init.ModTileEntityTypes;

@Mod(CreateRevision.MODID)
@Mod.EventBusSubscriber(modid = CreateRevision.MODID, bus = Bus.MOD)
public class CreateRevision{

	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MODID = "createrevision";

	public CreateRevision(){
		ModConfigs.register();

		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
	}

}