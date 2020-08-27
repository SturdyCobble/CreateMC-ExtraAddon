package com.sturdycobble.createrevision.init;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.geo.bedrock.BedrockAnvilBlock;
import com.sturdycobble.createrevision.contents.geo.bedrock.ObsidianDrillBlock;
import com.sturdycobble.createrevision.contents.geo.fan.GeothermalFanBlock;
import com.sturdycobble.createrevision.contents.heat.source.FrictionHeaterBlock;
import com.sturdycobble.createrevision.contents.heat.transfer.HeatExchangerBlock;
import com.sturdycobble.createrevision.contents.heat.transfer.HeatPipeBlock;
import com.sturdycobble.createrevision.contents.heat.transfer.ThermometerBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CreateRevision.MODID);
	
	public static final RegistryObject<Block> GEOTHERMAL_FAN = BLOCKS.register("geothermal_fan",
			() -> new GeothermalFanBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2F, 3.5F)));
	public static final RegistryObject<Block> COMPACT_WOOL = BLOCKS.register("compact_wool",
			() -> new Block(Block.Properties.create(Material.WOOL).hardnessAndResistance(0.5F, 1.5F)));
	public static final RegistryObject<Block> BEDROCK_SLATE = BLOCKS.register("bedrock_slate",
			() -> new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(9F, 1500F)));
	public static final RegistryObject<Block> BEDROCK_ANVIL = BLOCKS.register("bedrock_anvil",
			() -> new BedrockAnvilBlock(Block.Properties.create(Material.ANVIL).hardnessAndResistance(9F, 1500F)));
	public static final RegistryObject<Block> OBSIDIAN_DRILL = BLOCKS.register("obsidian_drill",
			() -> new ObsidianDrillBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6F, 1200F)));
	public static final RegistryObject<Block> HEAT_PIPE = BLOCKS.register("heat_pipe",
			() -> new HeatPipeBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6F, 6F)));
	public static final RegistryObject<Block> THERMOMETER = BLOCKS.register("thermometer",
			() -> new ThermometerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 0.5F)));
	public static final RegistryObject<Block> FRICTION_HEATER = BLOCKS.register("friction_heater",
			() -> new FrictionHeaterBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F, 1.0F)));
	public static final RegistryObject<Block> HEAT_EXCHANGER = BLOCKS.register("heat_exchanger",
			() -> new HeatExchangerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(6F, 6F)));
	
	public static final void blockRenderLayer() {
		RenderTypeLookup.setRenderLayer(ModBlocks.GEOTHERMAL_FAN.get(), RenderType.getCutoutMipped());
	}

}