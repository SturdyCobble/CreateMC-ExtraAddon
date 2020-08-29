package com.sturdycobble.createrevision.contents.heat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.Iterate;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;
import com.sturdycobble.createrevision.utils.HeatUtils.FacingDistance;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {

	public boolean checkConnection;

	private double heatCapacity = 3;
	private double conductivity = 0.7;
	private boolean isNode = false;
	private Map<IHeatableTileEntity, FacingDistance> neighborMap;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
		heatCapacity = 3;
		conductivity = 0.7;
		isNode = false;
		checkConnection = true;
		neighborMap = new HashMap<IHeatableTileEntity, FacingDistance>();
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(300, heatCapacity));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainer.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (checkConnection = true) {
			updateConnection();
			checkConnection = false;
		}

		if (world.getWorldInfo().getGameTime() % 5 == 0) {
			double temp;

			if (isNode) {
				temp = this.heatContainer.orElse(null).getTemp();
				temp += HeatUtils.getHeatCurrent(neighborMap, temp, conductivity) / heatCapacity;

			} else {
				temp = getEdgeTemp();
			}

			this.heatContainer.orElse(null).setTemp(temp);
		}
	}

	private double getEdgeTemp() {
		int count = 0;
		double sumTemp = 0;
		for (IHeatableTileEntity node : neighborMap.keySet()) {
			Direction facingDirection = neighborMap.get(node).getDirection().getOpposite();
			count++;
			LazyOptional<HeatContainer> nodeHeatContainer = node.getCapability(CapabilityHeat.HEAT_CAPABILITY, facingDirection);
			double nodeTemp = nodeHeatContainer.orElse(null).getTemp();
			sumTemp += nodeTemp;
		}
		return sumTemp / count;
	}

	@Override
	public void updateConnection() {
		isNode = isNode();
		neighborMap = findNeighborNode();
		for (IHeatableTileEntity neighbor : neighborMap.keySet())
			neighbor.markConnection();
	}

	@Override
	public boolean isNode() {
		int connectedAxis = 0;
		int connectedDirection = 0;

		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for (Axis axis : Iterate.axes) {
			Direction d1 = Direction.getFacingFromAxis(AxisDirection.NEGATIVE, axis);
			mpos.setPos(pos.offset(d1));
			TileEntity te1 = this.getWorld().getTileEntity(mpos);
			Direction d2 = Direction.getFacingFromAxis(AxisDirection.POSITIVE, axis);
			mpos.setPos(pos.offset(d2));
			TileEntity te2 = this.getWorld().getTileEntity(mpos);

			if ((te1 != null && te1 instanceof IHeatableTileEntity) || (te2 != null && te2 instanceof IHeatableTileEntity)) {
				connectedAxis++;
				if ((te1 != null && te1 instanceof IHeatableTileEntity)) {
					connectedDirection++;
					if (!(te1 instanceof HeatPipeTileEntity))
						return true;
				} else {
					connectedDirection++;
					if (!(te2 instanceof HeatPipeTileEntity))
						return true;
				}
			}
		}

		if (connectedAxis > 1 || connectedDirection < 2)
			return true;
		return false;
	}

	public Map<IHeatableTileEntity, FacingDistance> findNeighborNode() {
		return HeatUtils.findPipeNeighborNodes(world, pos);
	}

	public Map<IHeatableTileEntity, FacingDistance> getNeighborMap() {
		return neighborMap;
	}

	@Override
	public void markConnection() {
		checkConnection = true;
	}

}
