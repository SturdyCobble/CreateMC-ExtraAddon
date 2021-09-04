package sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.api.heat.HeatContainer;
import sturdycobble.createrevision.api.heat.HeatNode;
import sturdycobble.createrevision.api.heat.SimpleWritableHeatContainer;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.init.ModRecipeTypes;
import sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

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
    private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);
    public List<ItemStack> tileItems = new ArrayList<>();
    public BeltProcessingBehaviour processingBehaviour;
    private double sourcePower = 0.2;

    public FrictionHeaterTileEntity() {
        super(ModTileEntityTypes.FRICTION_HEATER.get());
    }

    private static List<ItemStack> applyRecipeOn(ItemStack stackIn, IRecipe<?> recipe) {
        ItemStack out = recipe.getResultItem().copy();
        List<ItemStack> stacks = ItemHelper.multipliedOutput(stackIn, out);
        return stacks;
    }

    private boolean isValidSide(@Nullable Direction side) {
        return side == null ? true : side.getAxis() != getBlockState().getValue(FACING).getAxis();
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
        Direction facing = getBlockState().getValue(FACING);
        return this.getLevel().getBlockState(this.getBlockPos().relative(facing)).getBlock().is(Blocks.STONE);
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
        super.tick();
        if (this.getLevel().getLevelData().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
            heatContainer.addHeat(getPower());
            node.updateTemp();
            setChanged();
        }
        doProcessing();
    }

    public TransportedResult applyProcessing(TransportedItemStack transported, World world) {
        Optional<FrictionRecipe> recipe = getRecipe(transported);
        if (isValidRecipe(recipe)) {
            if (transported.processingTime > recipe.orElse(null).getProcessingDuration()) {
                List<ItemStack> stacks = applyRecipeOn(transported.stack, recipe.get());
                List<TransportedItemStack> transportedStacks = new ArrayList<>();
                for (ItemStack additional : stacks) {
                    TransportedItemStack newTransported = transported.getSimilar();
                    newTransported.stack = additional.copy();
                    transportedStacks.add(newTransported);
                }
                return TransportedResult.convertTo(transportedStacks);
            } else {
                transported.processingTime += getRunningTickSpeed();
            }
        }
        return TransportedResult.doNothing();
    }

    public void doProcessing() {
        TransportedItemStackHandlerBehaviour handler = TileEntityBehaviour.get(this.getLevel(), this.getBlockPos().below(1),
                TransportedItemStackHandlerBehaviour.TYPE);
        if (handler == null)
            return;
        handler.handleProcessingOnAllItems((transported) -> {
            if (this.getLevel().isClientSide())
                return TransportedResult.doNothing();
            return applyProcessing(transported, this.getLevel());
        });
        sendData();
        setChanged();
        //markDirty();
    }

    private int getRunningTickSpeed() {
        return Math.round(Math.abs(getSpeed()) / 4);
    }

    public boolean isValidRecipe(Optional<FrictionRecipe> recipe) {
        if (!recipe.isPresent())
            return false;
        return recipe.orElse(null).isValidTemp(heatContainer.getTemp());
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
    protected void fromTag(BlockState state, CompoundNBT tag, boolean clientPacket) {
        heatContainer.deserializeNBT(tag.getCompound("heat"));
        super.fromTag(state, tag, clientPacket);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.put("heat", heatContainer.serializeNBT());
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        heatContainer.deserializeNBT(nbt.getCompound("heat"));
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = super.getUpdatePacket().getTag();

        nbt.put("heat", heatContainer.serializeNBT());
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        heatContainer.deserializeNBT(nbt.getCompound("heat"));
        super.onDataPacket(net, pkt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityHeat.HEAT_CAPABILITY) {
            return isValidSide(side) ? heatContainerCap.cast() : LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    public Optional<FrictionRecipe> getRecipe(TransportedItemStack transportedStack) {
        RecipeWrapper internalInv = new RecipeWrapper(new ItemStackHandler(1));
        internalInv.setItem(0, transportedStack.stack);
        return ModRecipeTypes.FRICTION.find(internalInv, this.getLevel());
    }

}