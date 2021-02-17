package com.sturdycobble.createrevision.events;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.contents.heat.ThermometerTileEntity;
import com.sturdycobble.createrevision.init.ModItems;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CreateRevision.MODID, value = Dist.CLIENT)
public class TemperatureOverlayRenderer {
	@SubscribeEvent
	public static void showThermometerTooltip(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.HOTBAR)
			return;

		RayTraceResult objectMouseOver = Minecraft.getInstance().objectMouseOver;
		if (!(objectMouseOver instanceof BlockRayTraceResult))
			return;

		BlockRayTraceResult result = (BlockRayTraceResult) objectMouseOver;
		Minecraft mc = Minecraft.getInstance();
		ClientWorld world = mc.world;
		BlockPos pos = result.getPos();
		TileEntity te = world.getTileEntity(pos);
		ItemStack headSlot = mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		Direction lookingAtSide = Direction.getFacingDirections(mc.player)[0].getOpposite();
		Item displayIconItem = ModItems.IR_GOGGLES.get();

		boolean wearingGoggles = ModItems.IR_GOGGLES.get() == headSlot.getItem();

		List<String> tooltip = new ArrayList<>();

		if (te == null)
			return;

		LazyOptional<HeatContainer> lookingTileCap = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, lookingAtSide);

		if (wearingGoggles) {
			if (lookingTileCap.isPresent() && !mc.player.isSneaking()) {
				tooltip.add("    Temperature Information");
				DecimalFormat deciamlFormat = new DecimalFormat("#,###");
				tooltip.add("      " + deciamlFormat.format(lookingTileCap.orElse(null).getTemp()) + "K");
				tooltip.add("");
			}

			boolean goggleInformation = te instanceof IHaveGoggleInformation;
			boolean hoveringInformation = te instanceof IHaveHoveringInformation;

			if (goggleInformation) {
				IHaveGoggleInformation gte = (IHaveGoggleInformation) te;
				if (!gte.addToGoggleTooltip(tooltip, mc.player.isSneaking()))
					goggleInformation = false;
			}

			if (hoveringInformation) {
				boolean goggleAddedInformation = !tooltip.isEmpty();
				if (goggleAddedInformation)
					tooltip.add("");
				IHaveHoveringInformation hte = (IHaveHoveringInformation) te;
				if (!hte.addToTooltip(tooltip, mc.player.isSneaking()))
					hoveringInformation = false;
				if (goggleAddedInformation && !hoveringInformation)
					tooltip.remove(tooltip.size() - 1);
			}
		}

		if (te instanceof ThermometerTileEntity) {
			tooltip.add("    Temperature Information");

			ThermometerTileEntity thermoTE = (ThermometerTileEntity) te;

			DecimalFormat deciamlFormat = new DecimalFormat("#,###");
			if (thermoTE.getTemp() == -1) {
				tooltip.add("       No Temp Data");
			} else {
				tooltip.add("      " + deciamlFormat.format(thermoTE.getTemp()) + "K");
			}
			tooltip.add("");
			displayIconItem = ModItems.THERMOMETER.get();
		}

		if (tooltip.isEmpty())
			return;

		RenderSystem.pushMatrix();
		Screen tooltipScreen = new TooltipScreen(null);
		tooltipScreen.init(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());

		int posX = tooltipScreen.width / 2;
		int posY = tooltipScreen.height / 2;

		tooltipScreen.renderTooltip(tooltip, posX, posY);

		GuiGameElement.of(displayIconItem).at(posX + 10, posY - 16).render();
		RenderSystem.popMatrix();
	}

	private static final class TooltipScreen extends Screen {
		private TooltipScreen(ITextComponent text) {
			super(text);
		}

		@Override
		public void init(Minecraft mc, int width, int height) {
			this.minecraft = mc;
			this.itemRenderer = mc.getItemRenderer();
			this.font = mc.fontRenderer;
			this.width = width;
			this.height = height;
		}
	}

}
