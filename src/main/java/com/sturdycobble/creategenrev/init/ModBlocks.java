package com.sturdycobble.creategenrev.init;

import com.sturdycobble.creategenrev.CreateGeneratorRevision;

import net.minecraft.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * Block Registration.
 * 
 * @author StdCobble
 *
 */

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CreateGeneratorRevision.MODID);
	
	public ModBlocks() {

	}

}