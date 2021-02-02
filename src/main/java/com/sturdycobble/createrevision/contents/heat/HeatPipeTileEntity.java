package com.sturdycobble.createrevision.contents.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.HeatNode;
import com.sturdycobble.createrevision.api.heat.SimpleWritableHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTileEntity extends SyncedTileEntity implements ITickableTileEntity {

	private final SimpleWritableHeatContainer heatContainer = new SimpleWritableHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getHeatPipeHeatCapacity();
		}

		@Override
		public double getConductivity() {
			return ModConfigs.getHeatPipeConductivity();
		}
	};

	private final HeatNode node = new HeatNode(this, heatContainer);

	private boolean isNode = true;

	public HeatPipeTileEntity() {
		super(ModTileEntityTypes.HEAT_PIPE.get());
	}

	private boolean isValidSide(@Nullable Direction side) {
		return side == null ? true : getBlockState().get(HeatPipeBlock.FACING_TO_PROPERTY_MAP.get(side));
	}

	public void updateAllNeighbors(BlockState state) {
		if (!world.isRemote) {
			isNode = HeatUtils.isPipeNode(this);
			for (Direction direction : Direction.values()) {
				if (isValidSide(direction)) {
					node.getConnection(direction).markForUpdate();
				}
			}
		}
	}

	public boolean isNode() {
		return isNode;
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
			node.updateTemp();
			markDirty();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag.put("heat", heatContainer.serializeNBT());
		tag.putBoolean("isNode", isNode);
		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag) {
		heatContainer.deserializeNBT(tag.getCompound("heat"));
		isNode = tag.getBoolean("isNode");
		super.read(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();

		nbt.put("heat", heatContainer.serializeNBT());
		nbt.putBoolean("isNode", isNode);
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		super.handleUpdateTag(nbt);
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
		isNode = nbt.getBoolean("isNode");
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();

		nbt.put("heat", heatContainer.serializeNBT());
		nbt.putBoolean("isNode", isNode);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
		isNode = nbt.getBoolean("isNode");
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
