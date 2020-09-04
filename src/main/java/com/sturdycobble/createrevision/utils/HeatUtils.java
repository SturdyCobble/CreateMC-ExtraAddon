package com.sturdycobble.createrevision.utils;

import java.util.HashMap;
import java.util.Map;

import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.contents.heat.HeatPipeBlock;
import com.sturdycobble.createrevision.contents.heat.HeatPipeTileEntity;
import com.sturdycobble.createrevision.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public final class HeatUtils {

	public static double getHeatCurrent(World world, BlockPos pos, Map<Direction, Integer> neighbors, double temp, double conductivity) {
		double heatCurrent = 0;
		for (Direction direction : neighbors.keySet()) {
			int distance = neighbors.get(direction);
			TileEntity te = world.getTileEntity(pos.offset(direction, distance));
			if (te != null) {
				LazyOptional<HeatContainer> nodeHeatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, direction.getOpposite());
				if (nodeHeatContainer.isPresent()) {
					double neighborTemp = nodeHeatContainer.orElse(null).getTemp();
					heatCurrent += (neighborTemp - temp) * conductivity / distance;
				}
			}
		}
		return heatCurrent;
	}
		
	public static Map<Direction, Integer> findPipeNeighborNodes(World world, BlockPos pos) {
		Map<Direction, Integer> nodes = new HashMap<Direction, Integer>();
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		
		for (Direction d : Direction.values()) {
			int count = 0;
			mpos.setPos(pos);
			while (count < 64) {
				count++;
				mpos.move(d);
				TileEntity te = world.getTileEntity(mpos);
				if (te != null) {
					HeatContainer nodeHeatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, d.getOpposite()).orElse(null);
					if (nodeHeatContainer != null) {
						if (te instanceof HeatPipeTileEntity) {
							if (!((HeatPipeTileEntity) te).isNode()) {
								continue;
							}
						}
						nodes.put(d, count);
					}
				}
				break;
			}
		}
		return nodes;
	}

	public static boolean isPipeNode(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (!HeatPipeBlock.isStraightPipe(world, pos, state))
			return true;

		boolean pipeConnectedOnlyAxisFound = true;
		for (Direction d : Direction.values()) {
			if (world.getBlockState(pos).get(HeatPipeBlock.FACING_TO_PROPERTY_MAP.get(d))) {
				if (!(world.getBlockState(pos.offset(d)).getBlock() == ModBlocks.HEAT_PIPE.get()
						&& world.getBlockState(pos.offset(d.getOpposite())).getBlock() == ModBlocks.HEAT_PIPE.get())) {
					pipeConnectedOnlyAxisFound = false;
				}
			}
		}
		return !pipeConnectedOnlyAxisFound;
	}

}
