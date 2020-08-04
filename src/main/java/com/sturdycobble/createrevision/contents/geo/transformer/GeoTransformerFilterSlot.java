package com.sturdycobble.createrevision.contents.geo.transformer;

/**
 * Geothermal Transformer Filter Slot Class
 * Slightly Modified Version of com.simibubi.create.content.contraptions.components.saw.SawFilterSlot
 * 
 * @author SturdyCobble
 *
 */

public class GeoTransformerFilterSlot /*extends ValueBoxTransform */{
	/*@Override
	protected Vec3d getLocalOffset(BlockState state) {
		if (state.get(GeoTransformerBlock.FACING) != Direction.UP)
			return null;
		Vec3d x = VecHelper.voxelSpace(8f, 12.5f, 12.25f);
		Vec3d z = VecHelper.voxelSpace(12.25f, 12.5f, 8f);
		return state.get(GeoTransformerBlock.AXIS_ALONG_FIRST_COORDINATE) ? z : x;
	}

	@Override
	protected void rotate(BlockState state, MatrixStack ms) {
		int yRot = state.get(GeoTransformerBlock.AXIS_ALONG_FIRST_COORDINATE) ? 270 : 180;
		MatrixStacker.of(ms)
			.rotateY(yRot)
			.rotateX(90);
	}
*/
}
