package com.sturdycobble.createrevision.contents.heat;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTileEntity extends SyncedTileEntity implements ITickableTileEntity {
	
	private final double conductivity = ModConfigs.getHeatPipeConductivity();
	private boolean isNode = false;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
	}
	
	private final SimpleHeatContainer heatContainer = new SimpleHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getHeatPipeHeatCapacity();
		}
	};
	
	private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainerCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % 20 == 0) {
			double temp;

			if (isNode) {
				temp = heatContainer.getTemp();
				temp += HeatUtils.getHeatCurrent(world, pos, heatContainer.getNeighbors(), temp, conductivity) / heatContainer.getCapacity();
				CreateRevision.LOGGER.info("HEAT CURRENT" + HeatUtils.getHeatCurrent(world, pos, heatContainer.getNeighbors(), temp, conductivity) );
				CreateRevision.LOGGER.info("HEAT CURRENT" + HeatUtils.getHeatCurrent(world, pos, heatContainer.getNeighbors(), temp, conductivity) / heatContainer.getCapacity());
			} else {
				temp = getEdgeTemp();
			}
			heatContainer.setTemp(temp);
			markDirty();
		}
	}

	private double getEdgeTemp() {
		int count = 0;
		double sumTemp = 0;
		double nodeTemp = 0;
		Map<Direction, Integer> neighborMap = heatContainer.getNeighbors();
		for (Direction direction : neighborMap.keySet()) {
			int distance = neighborMap.get(direction);
			TileEntity te = world.getTileEntity(pos.offset(direction, distance));
			if (te != null) {
				LazyOptional<HeatContainer> nodeHeatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, direction.getOpposite());
				if (nodeHeatContainer.isPresent()) {
					nodeTemp = nodeHeatContainer.orElse(null).getTemp();
					sumTemp += nodeTemp;
					count++;
				}
			}
		}
		return count == 0 ? 300 : sumTemp / count;
	}

	@Override
    public CompoundNBT write(CompoundNBT tag) {
		tag = heatContainer.serializeNBT();
        return super.write(tag);
    }
    
	@Override
	public void read(CompoundNBT tag) {
		heatContainer.deserializeNBT(tag);
		super.read(tag);
	}
	
	public boolean isNode() {
		return isNode;
	}

	public void updateAllNeighbors() {
		isNode = HeatUtils.isPipeNode(world, pos);
		heatContainer.setNeighbors(HeatUtils.findPipeNeighborNodes(world, pos));
	}

	public void notifyNeighbors() {
		Map<Direction, Integer> neighborMap = heatContainer.getNeighbors();
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for (Direction d : neighborMap.keySet()) {
			mpos.setPos(pos);
			for (int distance = 1; distance <= neighborMap.get(d); distance++) {
				mpos.move(d);
				TileEntity te = world.getTileEntity(mpos);
				if (te != null && te instanceof HeatPipeTileEntity)
					((HeatPipeTileEntity) te).updateAllNeighbors();
			}
		}
	}

}
