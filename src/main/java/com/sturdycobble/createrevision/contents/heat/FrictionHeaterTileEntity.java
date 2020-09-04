package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FrictionHeaterTileEntity extends KineticTileEntity implements ITickableTileEntity {

	private final double conductivity = ModConfigs.getFrictionHeaterConductivity();
	private double sourcePower = 0.2;

	public FrictionHeaterTileEntity() {
		super(ModTileEntityTypes.FRICTION_HEATER.get());
	}
	
	private final SimpleHeatContainer heatContainer = new SimpleHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getFrictionHeaterHeatCapacity();
		}
	};

	private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			Direction facing = getBlockState().get(FACING);
			if (side.getAxis() != facing.getAxis())
				return heatContainerCap.cast();
			else return LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % 3 == 0) {
			double temp = heatContainer.getTemp();
			double heatCurrent = HeatUtils.getHeatCurrent(world, pos, heatContainer.getNeighbors(), temp, conductivity);

			temp += (heatCurrent + getPower()) / heatContainer.getCapacity();

			heatContainer.setTemp(temp);
			markDirty();
		}
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

	public double getPower() {
		return isFrontBlocked() ? MathHelper.clamp(sourcePower * Math.abs(getSpeed()) - 0.12 * (heatContainer.getTemp() - 300), 0, 100) : 0;
	}

	public boolean isFrontBlocked() {
		Direction facing = getBlockState().get(FACING);
		return world.getBlockState(pos.offset(facing)).getBlock().isIn(Blocks.STONE);
	}

	@Override
	public float calculateStressApplied() {
		float impact = ModConfigs.getFrictionHeaterStress();
		return impact;
	}

	public boolean isValidDirection(@Nonnull Direction direction, Direction facing) {
		if (direction.getAxis() == facing.getOpposite().getAxis())
			return false;
		return true;
	}
	
	public void updateAllNeighbors(BlockState state) {
		Direction facing = state.get(FACING);
		for (Direction direction : Direction.values()) {
			if (isValidDirection(direction, facing)) {
				TileEntity te = world.getTileEntity(pos.offset(direction));
				if (te != null) {
					LazyOptional<HeatContainer> neighborContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, direction.getOpposite());
					if (neighborContainer.isPresent())
						heatContainer.putNeighbor(direction, 1);
					else
						heatContainer.removeNeighbor(direction);
				}
			}
		}
	}

}
