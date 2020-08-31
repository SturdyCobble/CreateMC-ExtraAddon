package com.sturdycobble.createrevision.contents.heat;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;
import com.sturdycobble.createrevision.utils.HeatUtils.FacingDistance;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {

	public boolean checkConnection = true;

	private final double heatCapacity = ModConfigs.heatPipeHeatCapacity.get();
	private final double conductivity = ModConfigs.heatPipeConductivity.get();
	private boolean isNode = false;

	private Map<IHeatableTileEntity, FacingDistance> neighborMap;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
		neighborMap = new HashMap<IHeatableTileEntity, FacingDistance>();
	}
	
	private final LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(heatCapacity));

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

		if (world.getWorldInfo().getGameTime() % 3 == 0) {
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
		double nodeTemp = 0;
		for (IHeatableTileEntity node : neighborMap.keySet()) {
			Direction facingDirection = neighborMap.get(node).getDirection().getOpposite();
			HeatContainer nodeHeatContainer 
					= node.getCapability(CapabilityHeat.HEAT_CAPABILITY, facingDirection).orElse(null);
			if (nodeHeatContainer != null) {
				nodeTemp = nodeHeatContainer.getTemp();
				sumTemp += nodeTemp;
				count++;
			}
		}
		return count == 0 ? 300 : sumTemp / count;
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
		return HeatUtils.isPipeNode(world, pos);
	}

	@Override
    public CompoundNBT write(CompoundNBT tag) {
		tag = heatContainer.orElse(null).serializeNBT();
        return super.write(tag);
    }
    
	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		heatContainer.orElse(null).deserializeNBT(tag);
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
