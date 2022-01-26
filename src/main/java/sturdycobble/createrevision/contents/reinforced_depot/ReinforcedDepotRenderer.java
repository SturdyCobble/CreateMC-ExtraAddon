package sturdycobble.createrevision.contents.reinforced_depot;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.content.contraptions.relays.belt.BeltHelper;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.block.depot.DepotRenderer;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ReinforcedDepotRenderer extends SafeTileEntityRenderer<ReinforcedDepotBlockEntity> {

    public ReinforcedDepotRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void renderItemsOf(SmartTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                     int light, int overlay, ReinforcedDepotBehaviour depotBehaviour) {

        TransportedItemStack transported = depotBehaviour.heldItem;
        TransformStack msr = TransformStack.cast(ms);
        Vec3 itemPosition = VecHelper.getCenterOf(te.getBlockPos());

        ms.pushPose();
        ms.translate(.5f, 14 / 16f, .5f);

        if (transported != null)
            depotBehaviour.incoming.add(transported);

        // Render main items
        for (TransportedItemStack tis : depotBehaviour.incoming) {
            ms.pushPose();
            msr.nudge(0);
            float offset = Mth.lerp(partialTicks, tis.prevBeltPosition, tis.beltPosition);
            float sideOffset = Mth.lerp(partialTicks, tis.prevSideOffset, tis.sideOffset);

            if (tis.insertedFrom.getAxis()
                    .isHorizontal()) {
                Vec3 offsetVec = Vec3.atLowerCornerOf(tis.insertedFrom.getOpposite()
                        .getNormal()).scale(.5f - offset);
                ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
                boolean alongX = tis.insertedFrom.getClockWise()
                        .getAxis() == Direction.Axis.X;
                if (!alongX)
                    sideOffset *= -1;
                ms.translate(alongX ? sideOffset : 0, 0, alongX ? 0 : sideOffset);
            }

            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = new Random(0);
            DepotRenderer.renderItem(ms, buffer, light, overlay, itemStack, angle, r, itemPosition);
            ms.popPose();
        }

        if (transported != null)
            depotBehaviour.incoming.remove(transported);

        // Render output items
        for (int i = 0; i < depotBehaviour.processingOutputBuffer.getSlots(); i++) {
            ItemStack stack = depotBehaviour.processingOutputBuffer.getStackInSlot(i);
            if (stack.isEmpty())
                continue;
            ms.pushPose();
            msr.nudge(i);

            boolean renderUpright = BeltHelper.isItemUpright(stack);
            msr.rotateY(360 / 8f * i);
            ms.translate(.35f, 0, 0);
            if (renderUpright)
                msr.rotateY(-(360 / 8f * i));
            Random r = new Random(i + 1);
            int angle = (int) (360 * r.nextFloat());
            DepotRenderer.renderItem(ms, buffer, light, overlay, stack, renderUpright ? angle + 90 : angle, r, itemPosition);
            ms.popPose();
        }

        ms.popPose();
    }

    @Override
    protected void renderSafe(ReinforcedDepotBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        renderItemsOf(te, partialTicks, ms, buffer, light, overlay, te.depotBehaviour);
    }

}
