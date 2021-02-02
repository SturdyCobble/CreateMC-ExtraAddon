package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.HeatNode;
import com.sturdycobble.createrevision.api.heat.SimpleWritableHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class FrictionHeaterTileEntity extends KineticTileEntity implements ITickableTileEntity {

	private final SimpleWritableHeatContainer heatContainer = new SimpleWritableHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getHeatPipeHeatCapacity();
		}

		@Override
		public double getConductivity() {
			return ModConfigs.getFrictionHeaterConductivity();
		}
	};

	private final HeatNode node = new HeatNode(this, heatContainer);
	private double sourcePower = 0.2;

	public FrictionHeaterTileEntity() {
		super(ModTileEntityTypes.FRICTION_HEATER.get());
	}

	private boolean isValidSide(@Nullable Direction side) {
		return side == null ? true : side.getAxis() != getBlockState().get(FACING).getAxis();
	}

	public void updateAllNeighbors(BlockState state) {
		for (Direction direction : Direction.values()) {
			if (isValidSide(direction)) {
				node.getConnection(direction).markForUpdate();
			}
		}
	}

	public double getPower() {
		return isFrontBlocked()
				? MathHelper.clamp(sourcePower * Math.abs(getSpeed()) - 0.12 * (heatContainer.getTemp() - 300), 0, 100)
				: 0;
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

	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
			heatContainer.addHeat(getPower());
			node.updateTemp();
			markDirty();
		}
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
	}

	@Override
	protected void write(CompoundNBT tag, boolean clientPacket) {
		tag.put("heat", heatContainer.serializeNBT());
		super.write(tag, clientPacket);
	}

	@Override
	protected void read(CompoundNBT tag, boolean clientPacket) {
		heatContainer.deserializeNBT(tag.getCompound("heat"));
		super.read(tag, clientPacket);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.put("heat", heatContainer.serializeNBT());
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		super.handleUpdateTag(nbt);
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = super.getUpdatePacket().getNbtCompound();

		nbt.put("heat", heatContainer.serializeNBT());
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
		super.onDataPacket(net, pkt);
	}

	private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return isValidSide(side) ? heatContainerCap.cast() : LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

}