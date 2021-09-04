package sturdycobble.createrevision.contents.heat;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Iterate;
import sturdycobble.createrevision.api.heat.CapabilityHeat;
import sturdycobble.createrevision.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HeatPipeBlock extends SixWayBlock implements IWaterLoggable, ITE<HeatPipeTileEntity> {

    public HeatPipeBlock(Properties properties) {
        super(4 / 16f, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    public static boolean isPipe(BlockState state) {
        return state.getBlock() == ModBlocks.HEAT_PIPE.get();
    }

    public static boolean isSource(IWorld world, BlockPos pos, BlockState state, Direction direction) {
        TileEntity te = world.getBlockEntity(pos);
        return te != null && te.getCapability(CapabilityHeat.HEAT_CAPABILITY, direction).isPresent() && !(isPipe(state));
    }

    public static boolean canConnectTo(IWorld world, BlockPos pos, BlockState neighbor, Direction blockFacing) {
        return isPipe(neighbor) || isSource(world, pos, neighbor, blockFacing);
    }

    public static boolean shouldDrawRim(IBlockDisplayReader world, BlockPos pos, BlockState state, Direction direction) {
        if (!isPipe(state) || !state.getValue(PROPERTY_BY_DIRECTION.get(direction)))
            return false;

        BlockPos facingPos = pos.relative(direction);
        BlockState facingState = world.getBlockState(facingPos);

        if (!isPipe(facingState))
            return true;
        if (!isCornerOrEndPipe(world, pos, state))
            return false;
        if (isStraightPipe(world, facingPos, facingState) || (!shouldDrawCasing(world, pos, state)
                && shouldDrawCasing(world, facingPos, facingState)))
            return true;
        if (isCornerOrEndPipe(world, facingPos, facingState))
            return direction.getAxisDirection() == AxisDirection.POSITIVE;

        return false;
    }

    public static boolean isCornerOrEndPipe(IBlockDisplayReader world, BlockPos pos, BlockState state) {
        return isPipe(state) && !isStraightPipe(world, pos, state) && !shouldDrawCasing(world, pos, state);
    }

    public static boolean isStraightPipe(IBlockDisplayReader world, BlockPos pos, BlockState state) {
        if (!isPipe(state))
            return false;
        boolean axisFound = false;
        for (Axis axis : Axis.values()) {
            Direction d1 = Direction.fromAxisAndDirection(axis, AxisDirection.NEGATIVE);
            Direction d2 = d1.getOpposite();
            if (state.getValue(PROPERTY_BY_DIRECTION.get(d1)) && state.getValue(PROPERTY_BY_DIRECTION.get(d2)))
                if (axisFound)
                    return false;
                else
                    axisFound = true;
        }
        return axisFound;
    }

    public static boolean shouldDrawCasing(IBlockDisplayReader world, BlockPos pos, BlockState state) {
        if (!isPipe(state))
            return false;
        for (Axis axis : Axis.values()) {
            int connections = 0;
            for (Direction direction : Iterate.directions)
                if (direction.getAxis() != axis && state.getValue(PROPERTY_BY_DIRECTION.get(direction)))
                    connections++;
            if (connections > 2)
                return true;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, BlockStateProperties.WATERLOGGED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return updateBlockState(defaultBlockState(), context.getNearestLookingDirection(), null, context.getLevel(),
                context.getClickedPos()).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(ifluidstate.getType() == Fluids.WATER));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, IWorld world,
                                  BlockPos pos, BlockPos neighbourPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return updateBlockState(state, direction, direction.getOpposite(), world, pos);
    }

    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore,
                                       IWorld world, BlockPos pos) {
        for (Direction d : Direction.values())
            if (d != ignore)
                state = state.setValue(PROPERTY_BY_DIRECTION.get(d), canConnectTo(world, pos.relative(d),
                        world.getBlockState(pos.relative(d)), d.getOpposite()));

        Direction connectedDirection = null;
        for (Direction d : Direction.values()) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(d))) {
                if (connectedDirection != null)
                    return state;
                connectedDirection = d;
            }
        }

        if (connectedDirection != null)
            return state.setValue(PROPERTY_BY_DIRECTION.get(connectedDirection.getOpposite()), true);

        return state.setValue(PROPERTY_BY_DIRECTION.get(preferredDirection), true).setValue(
                PROPERTY_BY_DIRECTION.get(preferredDirection.getOpposite()), true);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (world.isClientSide()) return;
        withTileEntityDo(world, pos, te -> te.updateAllNeighbors(state));
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (world.isClientSide()) return;
        withTileEntityDo(world, pos, te -> te.updateAllNeighbors(state));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HeatPipeTileEntity();
    }

    @Override
    public Class<HeatPipeTileEntity> getTileEntityClass() {
        return HeatPipeTileEntity.class;
    }

}
