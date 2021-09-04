package sturdycobble.createrevision.contents.heat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.foundation.render.PartialBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
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
    protected void renderSafe(FrictionHeaterTileEntity te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffer, int light,
                              int overlay) {

        SuperByteBuffer superByteBuffer = PartialBufferer.getFacing(ModBlockPartials.FRICTION_PLATE, te.getBlockState());
        IVertexBuilder vb = buffer.getBuffer(RenderType.solid());

        float time = AnimationTickHolder.getRenderTime(te.getLevel());
        float angle = (time * te.getSpeed() * 3.0F / 10.0F) % 360.0F / 180.0F * 3.1415927F;

        superByteBuffer
                .rotate(Direction.NORTH, angle)
                .light(light).renderInto(ms, vb);
    }

}