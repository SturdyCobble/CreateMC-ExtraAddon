package sturdycobble.createrevision.contents.reinforced_depot;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.content.logistics.block.depot.SharedDepotBlockMethods;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import sturdycobble.createrevision.init.ModBlockEntityTypes;

public class ReinforcedDepotBlock extends Block implements ITE<ReinforcedDepotBlockEntity>, IWrenchable {

    public ReinforcedDepotBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_,
                               CollisionContext p_220053_4_) {
        return AllShapes.DEPOT;
    }

    @Override
    public Class<ReinforcedDepotBlockEntity> getTileEntityClass() {
        return ReinforcedDepotBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReinforcedDepotBlockEntity> getTileEntityType() {
        return ModBlockEntityTypes.REINFORCED_DEPOT.get();
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            ReinforcedDepotBehaviour behaviour = TileEntityBehaviour.get(worldIn, pos, ReinforcedDepotBehaviour.TYPE);
            if (behaviour != null) {
                ItemHelper.dropContents(worldIn, pos, behaviour.processingOutputBuffer);
                for (TransportedItemStack transportedItemStack : behaviour.incoming) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), transportedItemStack.stack);
                }

                if (!behaviour.getHeldItemStack().isEmpty()) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), behaviour.getHeldItemStack());
                }

                worldIn.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        if (ray.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        } else if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ReinforcedDepotBehaviour behaviour = (ReinforcedDepotBehaviour) TileEntityBehaviour.get(world, pos, ReinforcedDepotBehaviour.TYPE);
            if (behaviour == null) {
                return InteractionResult.PASS;
            } else if (!(Boolean) behaviour.canAcceptItems.get()) {
                return InteractionResult.SUCCESS;
            } else {
                ItemStack heldItem = player.getItemInHand(hand);
                boolean wasEmptyHanded = heldItem.isEmpty();
                boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);
                ItemStack mainItemStack = behaviour.getHeldItemStack();
                if (!mainItemStack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(mainItemStack);
                    behaviour.removeHeldItem();
                    world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F + Create.RANDOM.nextFloat());
                }

                ItemStackHandler outputs = behaviour.processingOutputBuffer;

                for (int i = 0; i < outputs.getSlots(); ++i) {
                    player.getInventory().placeItemBackInInventory(outputs.extractItem(i, 64, false));
                }

                if (!wasEmptyHanded && !shouldntPlaceItem) {
                    TransportedItemStack transported = new TransportedItemStack(heldItem);
                    transported.insertedFrom = player.getDirection();
                    transported.prevBeltPosition = 0.25F;
                    transported.beltPosition = 0.25F;
                    behaviour.setHeldItem(transported);
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    AllSoundEvents.DEPOT_SLIDE.playOnServer(world, pos);
                }

                behaviour.tileEntity.notifyUpdate();
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
        SharedDepotBlockMethods.onLanded(worldIn, entityIn);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        ReinforcedDepotBehaviour depotBehaviour = TileEntityBehaviour.get(worldIn, pos, ReinforcedDepotBehaviour.TYPE);
        if (depotBehaviour == null) {
            return 0;
        } else {
            float f = (float) depotBehaviour.getPresentStackSize();
            Integer max = depotBehaviour.maxStackSize.get();
            f /= (float) (max == 0 ? 64 : max);
            return Mth.clamp(Mth.floor(f * 14.0F) + (f > 0.0F ? 1 : 0), 0, 15);
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

}
