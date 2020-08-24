package com.sturdycobble.createrevision.contents.heat.transfer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sturdycobble.createrevision.contents.heat.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heat.HeatContainer;
import com.sturdycobble.createrevision.contents.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.contents.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {
	
	public boolean checkConnection;
	
	private double heatCapacity;
	private double conductivity;
	private boolean isNode;
	private Map<IHeatableTileEntity, Long> neighborMap;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
		heatCapacity = 3;
		conductivity = 0.7;
		isNode = false;
		checkConnection= true;
		neighborMap = new HashMap<IHeatableTileEntity, Long>();
	}
	
	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(300, heatCapacity, conductivity));
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainer.cast();
		}
		return super.getCapability(cap, null);
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
				double heatCurrent = 0;
				temp = this.heatContainer.orElse(null).getTemp();
				for( IHeatableTileEntity node : neighborMap.keySet()) {
					Long distance = neighborMap.get(node);
					if ( node != null) {
						LazyOptional<HeatContainer> nodeHeatContainer = node.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
						if (nodeHeatContainer.isPresent()) {
							double neighborTemp = nodeHeatContainer.orElse(null).getTemp();
							heatCurrent += (neighborTemp - temp)*conductivity/distance;
						}
					}
				}
				temp += heatCurrent/heatCapacity;
				
				if (world.getBlockState(pos.down()).getBlock() == Blocks.MAGMA_BLOCK) {
					temp = 500;
				} else if (world.getBlockState(pos.down()).getBlock() == Blocks.PACKED_ICE) {
					temp = 100;
				}
			} else {
				temp = getEdgeTemp();
			}
			this.heatContainer.orElse(null).setTemp(temp);
		}
	}

	private double getEdgeTemp() {
		int count = 0;
		double sumTemp = 0;
		for( IHeatableTileEntity node : neighborMap.keySet()) {
			count++;
			LazyOptional<HeatContainer> nodeHeatContainer = node.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
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
		int connectedDirection = 0;
		int connectedAxis = 0;
		BlockPos.Mutable mpos1 = new BlockPos.Mutable();
		BlockPos.Mutable mpos2 = new BlockPos.Mutable();
		for (Direction d : Direction.values()) { 
			mpos1.setPos(this.getPos()).move(d);
			mpos2.setPos(this.getPos()).move(d.getOpposite());
			TileEntity te1 = this.getWorld().getTileEntity(mpos1);
			TileEntity te2 = this.getWorld().getTileEntity(mpos2);
			if ((te1 != null && te1 instanceof IHeatableTileEntity) || (te2 != null && te2 instanceof IHeatableTileEntity)) {
				connectedAxis++;
			}
			if ((te1 != null && te1 instanceof IHeatableTileEntity)) {
				connectedDirection++;
			}
		}
		if (connectedAxis > 2 || connectedDirection < 2) 
			return true;
		return false;
	}
	
	@Override
	public Map<IHeatableTileEntity, Long> findNeighborNode() {
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		TileEntity te;
		Map<IHeatableTileEntity, Long> nodeMap = new HashMap<IHeatableTileEntity, Long>();
		for (Direction d : Direction.values()) {
			long count = 0;
			mpos.setPos(this.getPos());
			while (count < 100) {
				count++;
				mpos.move(d);
				te = this.getWorld().getTileEntity(mpos);
				if (te != null && te instanceof IHeatableTileEntity) {
					if (((HeatPipeTileEntity) te).isNode() == true) {
						nodeMap.put((IHeatableTileEntity) world.getTileEntity(mpos), count);
						break;
					}
				} else {
					break;
				}
			}
		}
		return nodeMap;
	}
	
	@Override	
	public void markConnection() {
		checkConnection = true;
	}

}
