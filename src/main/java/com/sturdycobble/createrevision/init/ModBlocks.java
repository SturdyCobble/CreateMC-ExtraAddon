package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanBlock;

import com.sturdycobble.createrevision.contents.heatpipe.HeatPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.regex.MatchResult;

/**
 * Block Registration
 * 
 * @author SturdyCobble
 *
 */

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CreateRevision.MODID);
	public static final RegistryObject<Block> GEOTHERMAL_FAN = BLOCKS.register("geothermal_fan", 
			() -> new GeothermalFanBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2.5F,3.5F)));
	public static final RegistryObject<Block> COMPACT_WOOL = BLOCKS.register("compact_wool", 
			() -> new Block(Block.Properties.create(Material.WOOL).hardnessAndResistance(0.5F,1.5F)));
	public static final RegistryObject<Block> HEAT_PIPE = BLOCKS.register("heat_pipe",
			() -> new HeatPipeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2.5F,2.5F)));
	/*public static final RegistryObject<Block> GEO_TRANSFORMER = BLOCKS.register("geo_transformer",
			() -> new GeoTransformerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3F,3F)));*/
	
	public ModBlocks() {

	}

}