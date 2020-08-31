package com.sturdycobble.createrevision.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.contents.heat.HeatPipeBlock;
import com.sturdycobble.createrevision.init.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public final class HeatUtils {

	public static double getHeatCurrent(Map<IHeatableTileEntity, FacingDistance> neighbors, double temp, double conductivity) {
		double heatCurrent = 0;
		for (IHeatableTileEntity node : neighbors.keySet()) {
			Long distance = neighbors.get(node).getDistance();
			Direction facingDirection = neighbors.get(node).getDirection().getOpposite();
			if (node != null) {
				LazyOptional<HeatContainer> nodeHeatContainer = node.getCapability(CapabilityHeat.HEAT_CAPABILITY, facingDirection);
				if (nodeHeatContainer.isPresent()) {
					double neighborTemp = nodeHeatContainer.orElse(null).getTemp();
					heatCurrent += (neighborTemp - temp) * conductivity / distance;
				}
			}
		}
		return heatCurrent;
	}
	
	public static Map<IHeatableTileEntity, FacingDistance> findAdjacentNeighborNodes(World world, BlockPos pos, List<Direction> allowedDirections) {
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		TileEntity te;
		Map<IHeatableTileEntity, FacingDistance> nodeMap = new HashMap<IHeatableTileEntity, FacingDistance>();
		for (Direction d : allowedDirections) {
			mpos.setPos(pos);
			mpos.move(d);
			te = world.getTileEntity(mpos);
			if (te != null && te instanceof IHeatableTileEntity) {
				if (((IHeatableTileEntity) te).isNode() == true) {
					nodeMap.put((IHeatableTileEntity) world.getTileEntity(mpos), new FacingDistance(d, 1L));
				}
			}
		}
		return nodeMap;
	}
	
	public static Map<IHeatableTileEntity, FacingDistance> findPipeNeighborNodes(World world, BlockPos pos) {
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		TileEntity te;
		Map<IHeatableTileEntity, FacingDistance> nodeMap = new HashMap<IHeatableTileEntity, FacingDistance>();

		for (Direction d : Direction.values()) {
			long count = 0;
			mpos.setPos(pos);
			while (count < 100) {
				count++;
				mpos.move(d);
				te = world.getTileEntity(mpos);
				if (te != null && te instanceof IHeatableTileEntity) {
					if (((IHeatableTileEntity) te).isNode() == true) {
						nodeMap.put((IHeatableTileEntity) world.getTileEntity(mpos), new FacingDistance(d, count));
						break;
					}
				} else {
					break;
				}
			}
		}
		return nodeMap;
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

	public static class FacingDistance {

		private Direction direction;
		private long distance;

		public FacingDistance(Direction dir, long dist) {
			direction = dir;
			distance = dist;
		}

		public long getDistance() {
			return distance;
		}

		public Direction getDirection() {
			return direction;
		}

	}

}
