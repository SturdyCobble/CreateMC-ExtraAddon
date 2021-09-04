package sturdycobble.createrevision.events;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.utility.Color;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.api.heat.HeatContainer;
import sturdycobble.createrevision.contents.heat.ThermometerTileEntity;
import sturdycobble.createrevision.init.ModItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CreateRevision.MODID, value = Dist.CLIENT)
public class TemperatureOverlayRenderer {

    @SubscribeEvent
    public static void showThermometerTooltip(RenderGameOverlayEvent.Post event) {
        if (event.getType() != ElementType.HOTBAR)
            return;

        RayTraceResult objectMouseOver = Minecraft.getInstance().hitResult;
        if (!(objectMouseOver instanceof BlockRayTraceResult))
            return;

        BlockRayTraceResult result = (BlockRayTraceResult) objectMouseOver;
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.level;
        BlockPos pos = result.getBlockPos();
        TileEntity te = world.getBlockEntity(pos);
        ItemStack headSlot = mc.player.getItemBySlot(EquipmentSlotType.HEAD);
        Direction lookingAtSide = Direction.orderedByNearest(mc.player)[0].getOpposite();
        Item displayIconItem = ModItems.IR_GOGGLES.get();

        boolean wearingGoggles = ModItems.IR_GOGGLES.get() == headSlot.getItem();

        List<ITextComponent> tooltip = new ArrayList<>();

        if (te == null)
            return;

        LazyOptional<HeatContainer> lookingTileCap = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, lookingAtSide);

        if (wearingGoggles) {
            if (lookingTileCap.isPresent() && !mc.player.isSecondaryUseActive()) {
                tooltip.add(new StringTextComponent("    Temperature Information"));
                DecimalFormat deciamlFormat = new DecimalFormat("#,###");
                tooltip.add(new StringTextComponent("      " + deciamlFormat.format(lookingTileCap.orElse(null).getTemp()) + "K"));
                tooltip.add(new StringTextComponent(""));
            }

            boolean goggleInformation = te instanceof IHaveGoggleInformation;
            boolean hoveringInformation = te instanceof IHaveHoveringInformation;

            if (goggleInformation) {
                IHaveGoggleInformation gte = (IHaveGoggleInformation) te;
                if (!gte.addToGoggleTooltip(tooltip, mc.player.isSecondaryUseActive()))
                    goggleInformation = false;
            }

            if (hoveringInformation) {
                boolean goggleAddedInformation = !tooltip.isEmpty();
                if (goggleAddedInformation)
                    tooltip.add(new StringTextComponent(""));
                IHaveHoveringInformation hte = (IHaveHoveringInformation) te;
                if (!hte.addToTooltip(tooltip, mc.player.isSecondaryUseActive()))
                    hoveringInformation = false;
                if (goggleAddedInformation && !hoveringInformation)
                    tooltip.remove(tooltip.size() - 1);
            }
        }

        if (te instanceof ThermometerTileEntity) {
            tooltip.add(new StringTextComponent("    Temperature Information"));

            ThermometerTileEntity thermoTE = (ThermometerTileEntity) te;

            DecimalFormat deciamlFormat = new DecimalFormat("#,###");
            if (thermoTE.getTemp() == -1) {

                tooltip.add(new StringTextComponent("       No Temp Data"));
            } else {
                tooltip.add(new StringTextComponent("      " + deciamlFormat.format(thermoTE.getTemp()) + "K"));
            }
            tooltip.add(new StringTextComponent(""));
            displayIconItem = ModItems.THERMOMETER.get();
        }

        if (tooltip.isEmpty())
            return;

        MatrixStack ms = event.getMatrixStack();

        ms.pushPose();
        Screen tooltipScreen = new TooltipScreen(null);
        tooltipScreen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());

        int posX = tooltipScreen.width / 2;
        int posY = tooltipScreen.height / 2;

        Color colorBackground = Theme.c(Theme.Key.VANILLA_TOOLTIP_BACKGROUND).scaleAlpha(.75f);
        Color colorBorderTop = Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, true).copy();
        Color colorBorderBot = Theme.c(Theme.Key.VANILLA_TOOLTIP_BORDER, false).copy();

        GuiUtils.drawHoveringText(ms, tooltip, posX, posY, tooltipScreen.width, tooltipScreen.height, -1,
                colorBackground.getRGB(), colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);

        GuiGameElement.of(displayIconItem).at(posX + 10, posY - 16).render(ms);
        ms.popPose();
    }

    private static final class TooltipScreen extends Screen {

        private TooltipScreen(ITextComponent text) {
            super(text);
        }

        @Override
        public void init(Minecraft mc, int width, int height) {
            this.minecraft = mc;
            this.itemRenderer = mc.getItemRenderer();
            this.font = mc.font;
            this.width = width;
            this.height = height;
        }

    }

}
