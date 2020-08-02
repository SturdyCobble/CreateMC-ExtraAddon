package com.sturdycobble.creategenrev;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.sturdycobble.creategenrev.init.ModBlocks;
import com.sturdycobble.creategenrev.init.ModItems;
import com.sturdycobble.creategenrev.init.ModTags;
import com.sturdycobble.creategenrev.init.ModTileEntities;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
/**
 * Main Class for the Mod
 * 
 * @author StdCobble
 *
 */

@Mod(CreateGeneratorRevision.MODID)
public class CreateGeneratorRevision{
	
	public static final String MODID = "create_generator_revision";
	public static final String NAME = "Create MC Generator Revision Addon";
	public static final String VERSION = "0.0.1";
	
	public CreateGeneratorRevision() {
		MixinBootstrap.init();
		Mixins.addConfiguration("assets/creategenrev/creategenrev.mixins.json");
		
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModTags.register();
		// Register to ModEventBus
		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModTileEntities.TILE_ENTITY_TYPES.register(modEventBus);
	}
	
}
