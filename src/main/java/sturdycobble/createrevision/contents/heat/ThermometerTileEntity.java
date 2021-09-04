package sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.api.heat.HeatContainer;
import sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class ThermometerTileEntity extends TileEntity {

    public ThermometerTileEntity() {
        super(ModTileEntityTypes.THERMOMETER.get());
    }

    public double getTemp() {
        Direction facing = this.getLevel().getBlockState(this.getBlockPos()).getValue(FACING);
        TileEntity te = this.getLevel().getBlockEntity(this.getBlockPos().relative(facing.getOpposite()));
        double temp = -1;
        if (te != null) {
            LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
            LazyOptional<HeatContainer> heatContainerFacing = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, facing);
            if (heatContainerFacing.isPresent()) {
                temp = heatContainerFacing.orElse(null).getTemp();
            } else if (heatContainer.isPresent()) {
                temp = heatContainer.orElse(null).getTemp();
            }
        }
        return temp;
    }

}
