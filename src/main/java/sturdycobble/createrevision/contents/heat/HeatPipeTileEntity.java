package sturdycobble.createrevision.contents.heat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.api.heat.HeatContainer;
import sturdycobble.createrevision.api.heat.HeatNode;
import sturdycobble.createrevision.api.heat.SimpleWritableHeatContainer;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.init.ModTileEntityTypes;
import sturdycobble.createrevision.utils.HeatUtils;

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
    private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);
    private boolean isNode = true;

    public HeatPipeTileEntity() {
        super(ModTileEntityTypes.HEAT_PIPE.get());
    }

    private boolean isValidSide(@Nullable Direction side) {
        return side == null ? true : getBlockState().getValue(HeatPipeBlock.PROPERTY_BY_DIRECTION.get(side));
    }

    public void updateAllNeighbors(BlockState state) {
        if (!this.getLevel().isClientSide()) {
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
        if (this.getLevel().getLevelData().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
            node.updateTemp();
            sendData();
            setChanged();
            // markDirty();
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("heat", heatContainer.serializeNBT());
        tag.putBoolean("isNode", isNode);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        heatContainer.deserializeNBT(tag.getCompound("heat"));
        isNode = tag.getBoolean("isNode");
        super.load(state, tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("heat", heatContainer.serializeNBT());
        nbt.putBoolean("isNode", isNode);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        heatContainer.deserializeNBT(nbt.getCompound("heat"));
        isNode = nbt.getBoolean("isNode");
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.put("heat", heatContainer.serializeNBT());
        nbt.putBoolean("isNode", isNode);
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        heatContainer.deserializeNBT(nbt.getCompound("heat"));
        isNode = nbt.getBoolean("isNode");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityHeat.HEAT_CAPABILITY) {
            return isValidSide(side) ? heatContainerCap.cast() : LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

}