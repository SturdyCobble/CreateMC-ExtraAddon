package com.sturdycobble.createrevision.contents.geo.bedrock;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import com.sturdycobble.createrevision.init.ModBlockPartials;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;

public class BedrockAnvilPressRenderer extends KineticTileEntityRenderer {

	public BedrockAnvilPressRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer,
			int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

		BlockPos pos = te.getPos();
		BlockState blockState = te.getBlockState();
		int packedLightmapCoords = WorldRenderer.getPackedLightmapCoords(te.getWorld(), blockState, pos);
		float renderedPressOffset = ((BedrockAnvilTileEntity) te).getRenderedPressOffset(partialTicks);

		SuperByteBuffer pressRender = ModBlockPartials.BEDROCK_ANVIL_PRESS.renderOn(blockState);
		pressRender.translate(0, 0.7+renderedPressOffset, 0).light(packedLightmapCoords).renderInto(ms, buffer.getBuffer(RenderType.getSolid()));
	}
	
	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return shaft(getRotationAxisOf(te));
	}
}
