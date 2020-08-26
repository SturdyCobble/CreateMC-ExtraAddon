package com.sturdycobble.createrevision.contents.heat.transfer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import com.sturdycobble.createrevision.init.ModBlockPartials;
import com.sturdycobble.createrevision.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class ThermometerRenderer extends TileEntityRenderer<ThermometerTileEntity> {

	public ThermometerRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	protected void renderSafe(ThermometerTileEntity te, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffer, int light, int overlay) {
		if (te.getBlockState().getBlock() != ModBlocks.THERMOMETER.get() || !te.hasWorld())
			return;
		if (te.getBlockState().get(ThermometerBlock.FACING) == Direction.DOWN 
				|| te.getBlockState().get(ThermometerBlock.FACING) == Direction.UP)
			return;
		
		BlockState state = te.getBlockState();
		Direction facing = state.get(ThermometerBlock.FACING);
		int lightCoords = WorldRenderer.getPackedLightmapCoords(te.getWorld(), state, te.getPos());
		
		double temp = te.getTemp();
		float angle = (float) (MathHelper.clamp(temp, 0, 5000)/5000 * 3 * Math.PI / 2);

		ModBlockPartials.THERMOMETER_NEEDLE.renderOn(state);
		
		SuperByteBuffer needleBuffer = ModBlockPartials.THERMOMETER_NEEDLE.renderOn(state);
		
		IVertexBuilder vb = buffer.getBuffer(RenderType.getSolid());
		needleBuffer.rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing.rotateYCCW())));
		needleBuffer.translate(14.5f /16, 8f /16, 8f /16);
		needleBuffer.rotate(Direction.EAST, angle);
		needleBuffer.translate(-14.5f /16, -8f /16, -8f /16);
		needleBuffer.light(lightCoords).renderInto(ms, vb);
	}

	@Override
	public void render(ThermometerTileEntity te, float partialTicks, MatrixStack ms,
			IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		renderSafe(te, partialTicks, ms, buffer, combinedLight, combinedOverlay);
	}

}
