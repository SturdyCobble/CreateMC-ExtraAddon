package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.HashMap;
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

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FrictionHeaterTileEntity extends KineticTileEntity implements IHeatableTileEntity, ITickableTileEntity {

	public boolean checkConnection;

	private double heatCapacity;
	private double conductivity;
	private double sourcePower;
	private Map<IHeatableTileEntity, FacingDistance> neighborMap;

	public FrictionHeaterTileEntity() {
		super(ModTileEntityTypes.FRICTION_HEATER.get());
		heatCapacity = 4;
		conductivity = 0.3;
		checkConnection = true;
		sourcePower = 0.2;
		neighborMap = new HashMap<IHeatableTileEntity, FacingDistance>();
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(300, heatCapacity));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		Direction facing = world.getBlockState(pos).get(FACING);

		if (cap == CapabilityHeat.HEAT_CAPABILITY && side.getAxis() != facing.getAxis()) {
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
			double temp = this.heatContainer.orElse(null).getTemp();
			;
			double heatCurrent = HeatUtils.getHeatCurrent(neighborMap, temp, conductivity);

			temp += (heatCurrent + getPower()) / heatCapacity;

			this.heatContainer.orElse(null).setTemp(temp);
		}
	}

	@Override
	public void markConnection() {
		checkConnection = true;
	}

	public double getPower() {
		return isFrontBlocked() ? MathHelper.clamp(sourcePower * Math.abs(getSpeed()) - 0.1 * (heatContainer.orElse(null).getTemp() - 300), 0, 100) : 0;
	}

	public boolean isFrontBlocked() {
		Direction facing = world.getBlockState(pos).get(FACING);
		return world.getBlockState(pos.offset(facing)).getBlock().isIn(Blocks.STONE);
	}

	@Override
	public Map<IHeatableTileEntity, FacingDistance> findNeighborNode() {
		return HeatUtils.findAdjacentNeighborNodes(world, pos);
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
