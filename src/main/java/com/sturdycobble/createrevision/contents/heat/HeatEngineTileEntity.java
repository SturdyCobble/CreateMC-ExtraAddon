package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineTileEntity;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.WritableHeatContainer;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.LazyOptional;

public class HeatEngineTileEntity extends EngineTileEntity implements ITickableTileEntity {
	private LazyOptional<HeatContainer> hotContainerCap = LazyOptional.empty();
	private LazyOptional<HeatContainer> coolContainerCap = LazyOptional.empty();

	public HeatEngineTileEntity() {
		super(ModTileEntityTypes.HEAT_ENGINE.get());
	}

	@Override
	public void tick() {
		if (world.isRemote)
			return;
		if (world.getWorldInfo().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
			updateAdjacentHeatContainers();
		}
		super.lazyTick();
	}

	public void updateAdjacentHeatContainers() {
		TileEntity teHot = world.getTileEntity(EngineBlock.getBaseBlockPos(getBlockState(), pos));
		TileEntity teCool = world.getTileEntity(pos.offset(Direction.UP));

		Direction facing = getBlockState().get(HORIZONTAL_FACING);

		if ((teHot instanceof HeatExchangerTileEntity && teCool instanceof HeatExchangerTileEntity)) {
			hotContainerCap = teHot.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing.getOpposite());
			coolContainerCap = teCool.getCapability(CapabilityHeat.HEAT_CAPABILITY, Direction.DOWN);
		} else {
			hotContainerCap = LazyOptional.empty();
			coolContainerCap = LazyOptional.empty();
		}

		updateEngine();
	}

	public void updateEngine() {
		if (world.isRemote)
			return;
		WritableHeatContainer hotContainer = (WritableHeatContainer) hotContainerCap.orElse(null);
		WritableHeatContainer coolContainer = (WritableHeatContainer) coolContainerCap.orElse(null);
		if (hotContainer == null || coolContainer == null) {
			appliedCapacity = 0;
			appliedSpeed = 0;
			refreshWheelSpeed();
			return;
		}

		double tempHot = hotContainer.getTemp();
		double tempCool = coolContainer.getTemp();
		double heatFlowFromHot = tempHot * (tempHot - tempCool) / (tempHot + tempCool);
		double heatFlowToCool = tempCool * (tempHot - tempCool) / (tempHot + tempCool);
		double power = heatFlowFromHot - heatFlowToCool;
		float speed = (float) MathHelper.clamp(power * 0.5, -256, 256);
		float capacity = (speed == 0) ? 0 : 32;
		hotContainer.addHeat(-heatFlowFromHot);
		coolContainer.addHeat(heatFlowToCool);

		appliedCapacity = capacity;
		appliedSpeed = speed;
		refreshWheelSpeed();
	}

}
