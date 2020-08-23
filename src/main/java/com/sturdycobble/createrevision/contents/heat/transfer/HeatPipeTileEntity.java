package com.sturdycobble.createrevision.contents.heat.transfer;

import java.util.HashMap;
import java.util.Map;

import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.contents.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class HeatPipeTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {
	
	public boolean checkConnection;
	
	private double temp;
	private double heatCapacity;
	private double conductivity;
	private boolean isNode;
	private Map<IHeatableTileEntity, Long> neighborMap;
	private int tickCnt;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
		temp = 300;
		heatCapacity = 3;
		conductivity = 0.7;
		isNode = false;
		checkConnection= true;
		tickCnt = 0;
		neighborMap = new HashMap<IHeatableTileEntity, Long>();
	}
	
	@Override
	public void tick() {
		if (checkConnection = true) {
			updateConnection();
			checkConnection = false;
		}
		
		if(tickCnt > 10) {
			if (isNode) {
				double heatCurrent = 0;
				for( IHeatableTileEntity node : neighborMap.keySet()) {
					Long distance = neighborMap.get(node);
					double neighborTemp = node.getTemp();
					heatCurrent += (neighborTemp - temp)*conductivity/distance;
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
			tickCnt = 0;		
		}
		
		tickCnt ++;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putDouble("temp", temp);
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		temp = compound.getDouble("temp");
		super.read(compound);
	}

	@Override
	public double getTemp() {
		return temp; 
	}
	
	private double getEdgeTemp() {
		int count = 0;
		double sumTemp = 0;
		for( IHeatableTileEntity node : neighborMap.keySet()) {
			count++;
			sumTemp += node.getTemp();
		}
		return sumTemp / count;
	}

	@Override
	public void setTemp(int tempIn) {
		temp = tempIn;
	}

	@Override
	public double getCapacity() {
		return heatCapacity;
	}
	
	@Override
	public double getPower() {
		return 0;
	}
	
	public void updateConnection() {
		isNode = isNode();
		neighborMap = findNeighborNode();
		for (IHeatableTileEntity neighbor : neighborMap.keySet())
			neighbor.markConnection();
	}
	
	public void infoPrinter() {
		CreateRevision.logger.info("=====TEMP INFO=====");
		CreateRevision.logger.info("Current Temp : " + Math.floor(getTemp()));
		CreateRevision.logger.info("Is Node ? " + isNode );
		CreateRevision.logger.info("Neighbors ");
		for (Long dist : neighborMap.values())
			CreateRevision.logger.info(" --"+dist);
		CreateRevision.logger.info("===================");
	}

	private boolean isNode() {
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
	
	private Map<IHeatableTileEntity, Long> findNeighborNode() {
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
