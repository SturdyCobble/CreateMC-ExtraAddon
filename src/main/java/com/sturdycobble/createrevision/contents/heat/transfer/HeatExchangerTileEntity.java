package com.sturdycobble.createrevision.contents.heat.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sturdycobble.createrevision.contents.heat.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heat.HeatContainer;
import com.sturdycobble.createrevision.contents.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.contents.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatExchangerTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {
	
	
	public boolean checkConnection;
	
	private double heatCapacity;
	private double conductivity;
	private Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> neighborMap;
	private double heatExchanged;
	private int exchangeTime;

	public HeatExchangerTileEntity() {
		super(ModTileEntityTypes.HEAT_EXCHANGER.get());
		neighborMap = new HashMap<IHeatableTileEntity, SimpleEntry<Direction, Long>>();
		heatCapacity = 3;
		conductivity = 0.7;
		checkConnection= true;
		heatExchanged = 0;
		exchangeTime = 0;
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
			double temp = this.heatContainer.orElse(null).getTemp();
			double heatCurrent = getHeatCurrent(neighborMap, temp);
			
			setPowerWithInteraction(temp);
			double power = 0;
			if (exchangeTime > 0) {
				power = heatExchanged;
				exchangeTime--;
			}
			
			temp += (heatCurrent + power)/heatCapacity;
			this.heatContainer.orElse(null).setTemp(temp);
		}
	}
	
	public void setPowerWithInteraction(double temp) {
		Direction facing = world.getBlockState(pos).get(HeatExchangerBlock.FACING);
		BlockPos offsetPos = pos.offset(facing.getOpposite());
		Block offsetBlock = world.getBlockState(offsetPos).getBlock();
		
		if (offsetBlock == Blocks.MAGMA_BLOCK) {
			if (temp < 600) {
				world.setBlockState(offsetPos, Blocks.COBBLESTONE.getDefaultState());
				heatExchanged = 10D;
				exchangeTime = 12;
			}
		} else if (offsetBlock == Blocks.PACKED_ICE) {
			if (temp > 250) {
				world.setBlockState(offsetPos, Blocks.ICE.getDefaultState());
				heatExchanged = -5D;
				exchangeTime = 10;
			}
		} else if (offsetBlock == Blocks.ICE) {
			if (temp > 270) {
				world.setBlockState(offsetPos, Blocks.WATER.getDefaultState());
				heatExchanged = -3D;
				exchangeTime = 7;
			} else if (temp < 200) {
				world.setBlockState(offsetPos, Blocks.PACKED_ICE.getDefaultState());
				heatExchanged = 2D;
				exchangeTime = 3;
			}
		} else if (offsetBlock == Blocks.COBBLESTONE) {
			if (temp > 650) {
				world.setBlockState(offsetPos, Blocks.MAGMA_BLOCK.getDefaultState());
				heatExchanged = -1D;
				exchangeTime = 3;
			}
		} else if (offsetBlock == Blocks.WATER) {
			if (temp < 260) {
				world.setBlockState(offsetPos, Blocks.ICE.getDefaultState());
				heatExchanged = 1D;
				exchangeTime = 3;
			}
		}
	}

	@Override
	public void markConnection() {
		checkConnection = true;
	}

	@Override
	public Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> findNeighborNode() {
		Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> nodes 
				= new HashMap<IHeatableTileEntity, SimpleEntry<Direction, Long>>();
		for (Direction direction : Direction.values()) {
			if (world.getBlockState(pos.offset(direction)).getBlock() == ModBlocks.HEAT_PIPE.get())
				nodes.put((IHeatableTileEntity) world.getTileEntity(pos.offset(direction)), new SimpleEntry<Direction, Long>(direction, 1L));
		}
		return nodes;
	}

	@Override
	public void updateConnection() {
		neighborMap = findNeighborNode();
		for (IHeatableTileEntity neighbor : neighborMap.keySet())
			neighbor.markConnection();
	}

	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public Map<IHeatableTileEntity, SimpleEntry<Direction, Long>> getNeighborMap() {
		return neighborMap;
	}

	@Override
	public double getConductivity() {
		return conductivity;
	}
	
}
