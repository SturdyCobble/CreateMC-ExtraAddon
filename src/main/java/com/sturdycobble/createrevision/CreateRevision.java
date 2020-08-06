package com.sturdycobble.createrevision;

import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModItems;
import com.sturdycobble.createrevision.init.ModTags;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

// The value here should match an entry in the META-INF/mods.toml file

/**
 * Main Class for the Mod
 *
 * @author SturdyCobble
 */

@Mod(CreateRevision.MODID)
public class CreateRevision {

	public static final String MODID = "createrevision";
	public static final String NAME = "Create MC Revision Addon";
	public static final String VERSION = "0.0.1";

	public CreateRevision() {
		MixinBootstrap.init();
		Mixins.addConfiguration("assets/createrevision/createrevision.mixins.json");

		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModTags.register();
		// Register to ModEventBus
		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		modEventBus.addListener(this::preInit);
	}

	public void preInit(FMLCommonSetupEvent evt) {
		CapabilityHeat.register();
	}

}
