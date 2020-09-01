package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
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
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FrictionHeaterTileEntity extends KineticTileEntity implements IHeatableTileEntity, ITickableTileEntity {

	public boolean checkConnection = true;

	private final double heatCapacity = ModConfigs.heatPipeHeatCapacity.get();
	private final double conductivity = ModConfigs.heatPipeConductivity.get();
	private double sourcePower = 0.2;
	private Map<IHeatableTileEntity, FacingDistance> neighborMap;

	public FrictionHeaterTileEntity() {
		super(ModTileEntityTypes.FRICTION_HEATER.get());
		neighborMap = new HashMap<IHeatableTileEntity, FacingDistance>();
	}
	
	private final LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(heatCapacity));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		Direction facing = world.getBlockState(pos).get(FACING);

		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			if (side.getAxis() != facing.getAxis())
				return heatContainer.cast();
			else return LazyOptional.empty();
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
			double temp = this.heatContainer.orElse(null).getTemp();
			double heatCurrent = HeatUtils.getHeatCurrent(neighborMap, temp, conductivity);

			temp += (heatCurrent + getPower()) / heatCapacity;

			this.heatContainer.orElse(null).setTemp(temp);
			markDirty();
		}
	}

	@Override
    public CompoundNBT write(CompoundNBT tag) {
		tag = heatContainer.orElse(null).serializeNBT();
        return super.write(tag);
    }
    
	@Override
	public void read(CompoundNBT tag) {
		heatContainer.orElse(null).deserializeNBT(tag);
		super.read(tag);
	}

	@Override
	public void markConnection() {
		checkConnection = true;
	}

	public double getPower() {
		return isFrontBlocked() ? MathHelper.clamp(sourcePower * Math.abs(getSpeed()) - 0.12 * (heatContainer.orElse(null).getTemp() - 300), 0, 100) : 0;
	}

	public boolean isFrontBlocked() {
		Direction facing = world.getBlockState(pos).get(FACING);
		return world.getBlockState(pos.offset(facing)).getBlock().isIn(Blocks.STONE);
	}

	@Override
	public Map<IHeatableTileEntity, FacingDistance> findNeighborNode() {
		Direction facing = world.getBlockState(pos).get(FrictionHeaterBlock.FACING);
		List<Direction> allowedDirections = new ArrayList<Direction>(Arrays.asList(Direction.values()));
		allowedDirections.remove(facing);
		allowedDirections.remove(facing.getOpposite());
		return HeatUtils.findAdjacentNeighborNodes(world, pos, allowedDirections);
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
	public float calculateStressApplied() {
		float impact = ModConfigs.frictionHeaterStress.get().floatValue();
		return impact;
	}

	public Map<IHeatableTileEntity, FacingDistance> getNeighborMap() {
		return neighborMap;
	}

}
