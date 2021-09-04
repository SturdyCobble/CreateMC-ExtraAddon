package sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineTileEntity;
import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.api.heat.HeatContainer;
import sturdycobble.createrevision.api.heat.HeatNode;
import sturdycobble.createrevision.api.heat.WritableHeatContainer;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.init.ModTileEntityTypes;

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
        if (this.getLevel().isClientSide())
            return;
        if (this.getLevel().getLevelData().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
            updateAdjacentHeatContainers();
        }
        super.lazyTick();
    }

    public void updateAdjacentHeatContainers() {
        TileEntity teHot = this.getLevel().getBlockEntity(EngineBlock.getBaseBlockPos(getBlockState(), this.getBlockPos()));
        TileEntity teCool = this.getLevel().getBlockEntity(this.getBlockPos().relative(Direction.UP));

        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);

        if ((teHot instanceof HeatExchangerTileEntity && teCool instanceof HeatExchangerTileEntity)) {
            hotContainerCap = teHot.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing.getOpposite());
            coolContainerCap = teCool.getCapability(CapabilityHeat.HEAT_CAPABILITY, Direction.UP);
        } else {
            hotContainerCap = LazyOptional.empty();
            coolContainerCap = LazyOptional.empty();
        }

        updateEngine();
    }

    public void updateEngine() {
        if (this.getLevel().isClientSide())
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
        float speed = (float) MathHelper.clamp(ModConfigs.getHeatEngineEfficiency() * Math.round(power / 2), -256, 256);
        float capacity = (speed == 0) ? 0 : 32;
        hotContainer.addHeat(-heatFlowFromHot);
        coolContainer.addHeat(heatFlowToCool);

        appliedCapacity = capacity;
        appliedSpeed = speed;
        refreshWheelSpeed();
    }

}
