package com.sturdycobble.createrevision.init;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import com.simibubi.create.foundation.utility.SuperByteBufferCache.Compartment;
import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.CreateRevisionClient;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;

public class ModBlockPartials {

	public static final Compartment<Pair<Direction, ModBlockPartials>> DIRECTIONAL_PARTIAL = new Compartment<>();
	public static final Compartment<ModBlockPartials> PARTIAL = new Compartment<>();
	private static final List<ModBlockPartials> all = new ArrayList<>();
	private ResourceLocation modelLocation;
	private IBakedModel bakedModel;
	
	public static final ModBlockPartials OBSIDIAN_DRILL_HEAD = getBlockPartial("obsidian_drill_head");
	public static final ModBlockPartials BEDROCK_ANVIL_PRESS = getBlockPartial("bedrock_anvil_press");
	public static final ModBlockPartials HEAT_PIPE_CASING = getBlockPartial("heat_pipe/casing");
	
	public static final Map<Direction, ModBlockPartials> HEAT_PIPE_RIMS = new HashMap<>();
	
	static {
		populateMaps();
	}
	
	private static void populateMaps() {
		for (Direction d : Direction.values()) 
			HEAT_PIPE_RIMS.put(d, getBlockPartial("heat_pipe/rim/" + d.getName()));
	}
	
	public static void onModelRegistry(ModelRegistryEvent event) {
		for (ModBlockPartials partial : all)
			ModelLoader.addSpecialModel(partial.modelLocation);
	}

	public static void onModelBake(ModelBakeEvent event) {
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		for (ModBlockPartials partial : all)
			partial.bakedModel = modelRegistry.get(partial.modelLocation);
	}

	public static ModBlockPartials getBlockPartial(String path) {
		ModBlockPartials partials = new ModBlockPartials();
		partials.modelLocation = new ResourceLocation(CreateRevision.MODID, "block/" + path);
		all.add(partials);
		return partials;
	}

	public IBakedModel getModel() {
		return this.bakedModel;
	}

	public SuperByteBuffer renderOnDirectional(BlockState state) {
		Direction dir = state.get(FACING);
		return CreateRevisionClient.bufferCache.get(DIRECTIONAL_PARTIAL, Pair.of(dir, this),
				() -> new SuperByteBuffer(renderDirectionalPartial(this, state)));
	}
	
	public SuperByteBuffer renderOn(BlockState state) {
		return CreateRevisionClient.bufferCache.get(PARTIAL, this,
				() -> new SuperByteBuffer(renderPartial(this, state)));
	}

	public BufferBuilder renderDirectionalPartial(ModBlockPartials partial, BlockState state) {
		Direction facing = state.get(FACING);

		MatrixStack ms = new MatrixStack();

		MatrixStacker.of(ms).centre().rotateY(AngleHelper.horizontalAngle(facing))
				.rotateX(AngleHelper.verticalAngle(facing)).unCentre();

		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockModelRenderer blockRenderer = dispatcher.getBlockModelRenderer();
		BufferBuilder builder = new BufferBuilder(DefaultVertexFormats.BLOCK.getIntegerSize());
		Random random = new Random();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		blockRenderer.renderModelFlat(Minecraft.getInstance().world, partial.getModel(), state, BlockPos.ZERO.up(255),
				ms, builder, true, random, 42, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		builder.finishDrawing();
		return builder;
	}
	
	public BufferBuilder renderPartial(ModBlockPartials partial, BlockState state) {
		MatrixStack ms = new MatrixStack();

		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockModelRenderer blockRenderer = dispatcher.getBlockModelRenderer();
		BufferBuilder builder = new BufferBuilder(DefaultVertexFormats.BLOCK.getIntegerSize());
		Random random = new Random();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		blockRenderer.renderModelFlat(Minecraft.getInstance().world, partial.getModel(), state, BlockPos.ZERO.up(255),
				ms, builder, true, random, 42, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
		builder.finishDrawing();
		return builder;
	}
	
}