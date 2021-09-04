package sturdycobble.createrevision.contents.heat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import sturdycobble.createrevision.init.ModBlockPartials;
import sturdycobble.createrevision.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class ThermometerRenderer extends SafeTileEntityRenderer<ThermometerTileEntity> {

    public ThermometerRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void renderSafe(ThermometerTileEntity te, float partialTicks, MatrixStack ms,
                              IRenderTypeBuffer buffer, int light, int overlay) {
        if (te.getBlockState().getBlock() != ModBlocks.THERMOMETER.get() || !te.hasLevel())
            return;
        if (te.getBlockState().getValue(BlockStateProperties.FACING) == Direction.DOWN
                || te.getBlockState().getValue(BlockStateProperties.FACING) == Direction.UP)
            return;

        BlockState state = te.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.FACING);

        double temp = te.getTemp();
        float angle = (float) (MathHelper.clamp(temp, 0, 3000) / 3000 * 4 * Math.PI / 3);

        SuperByteBuffer needleBuffer = PartialBufferer.get(ModBlockPartials.THERMOMETER_NEEDLE, state);

        IVertexBuilder vb = buffer.getBuffer(RenderType.solid());

        needleBuffer
                .rotateCentered(Direction.UP, AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())))
                .translate(14.5f / 16, 8f / 16, 8f / 16)
                .rotate(Direction.EAST, angle)
                .translate(-14.5f / 16, -8f / 16, -8f / 16)
                .light(light).renderInto(ms, vb);
    }

}
