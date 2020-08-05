package com.sturdycobble.createrevision.utils;

import com.sturdycobble.createrevision.contents.heatSystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatSystem.HeatContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utils {
	public static HeatContainer getHeatContainer(TileEntity te) {
		if (te != null)
			return te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElseThrow(IllegalStateException::new);
		return null;
	}

	public static TileEntity getTileEntity(World world, BlockPos pos) {
		return world.getTileEntity(pos);
	}
}
