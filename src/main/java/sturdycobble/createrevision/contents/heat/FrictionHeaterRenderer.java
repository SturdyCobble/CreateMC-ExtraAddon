package sturdycobble.createrevision.contents.heat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.util.math.BlockPos;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.init.ModBlockPartials;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class FrictionHeaterRenderer extends SafeTileEntityRenderer<FrictionHeaterTileEntity> {

    public FrictionHeaterRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    protected void renderSafe(FrictionHeaterTileEntity te, float partialTicks, MatrixStack ms,
                              IRenderTypeBuffer buffer, int light, int overlay) {
        SuperByteBuffer superByteBuffer = PartialBufferer.getFacing(ModBlockPartials.FRICTION_PLATE, te.getBlockState());
        IVertexBuilder vb = buffer.getBuffer(RenderType.solid());

        BlockPos pos = te.getBlockPos();
        Direction.Axis axis = ((IRotate) te.getBlockState().getBlock()).getRotationAxis(te.getBlockState());

        KineticTileEntityRenderer.kineticRotationTransform(
                        superByteBuffer, te, axis, KineticTileEntityRenderer.getAngleForTe(te, pos, axis), light)
                .renderInto(ms, vb);
    }

}