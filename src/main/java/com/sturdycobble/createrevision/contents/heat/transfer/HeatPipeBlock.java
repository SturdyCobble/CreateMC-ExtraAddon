package com.sturdycobble.createrevision.contents.heat.transfer;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HeatPipeBlock extends SixWayBlock implements IWaterLoggable {

	public HeatPipeBlock(Properties properties) {
		super(4 / 16f, properties);
		this.setDefaultState(super.getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
	}
	
	public static boolean isPipe(BlockState state) {
        return state.getBlock() instanceof HeatPipeBlock;
    }
	
	public static boolean isSource(BlockState state) {
        return (state.getBlock() == Blocks.MAGMA_BLOCK || state.getBlock() == Blocks.PACKED_ICE);
    }

    public static boolean canConnectTo(ILightReader world, BlockPos pos, BlockState neighbor, Direction blockFace) {
        if (isPipe(neighbor) || isSource(neighbor))
            return true;
        return false;
    }

    public static boolean shouldDrawRim(ILightReader world, BlockPos pos, BlockState state, Direction direction) {
    	if (!isPipe(state))
            return false;
        if (!state.get(FACING_TO_PROPERTY_MAP.get(direction)))
            return false;
        BlockPos facingPos = pos.offset(direction);
        BlockState facingState = world.getBlockState(facingPos);
        if (!isPipe(facingState))
            return true;
        if (!isCornerOrEndPipe(world, pos, state))
            return false;
        if (isStraightPipe(world, facingPos, facingState))
            return true;
        if (!shouldDrawCasing(world, pos, state) && shouldDrawCasing(world, facingPos, facingState))
            return true;
        if (isCornerOrEndPipe(world, facingPos, facingState))
            return direction.getAxisDirection() == AxisDirection.POSITIVE;
        return false;
    }

    public static boolean isCornerOrEndPipe(ILightReader world, BlockPos pos, BlockState state) {
        return isPipe(state) && !isStraightPipe(world, pos, state) && !shouldDrawCasing(world, pos, state);
    }

    public static boolean isStraightPipe(ILightReader world, BlockPos pos, BlockState state) {
        if (!isPipe(state))
            return false;
        boolean axisFound = false;
        for (Axis axis : Iterate.axes) {
            Direction d1 = Direction.getFacingFromAxis(AxisDirection.NEGATIVE, axis);
            Direction d2 = Direction.getFacingFromAxis(AxisDirection.POSITIVE, axis);
            if (state.get(FACING_TO_PROPERTY_MAP.get(d1)) && state.get(FACING_TO_PROPERTY_MAP.get(d2)))
                if (axisFound)
                    return false;
                else
                    axisFound = true;
        }
        return axisFound;
    }

    public static boolean shouldDrawCasing(ILightReader world, BlockPos pos, BlockState state) {
        if (!isPipe(state))
            return false;
        for (Axis axis : Iterate.axes) {
            int connections = 0;
            for (Direction direction : Iterate.directions)
                if (direction.getAxis() != axis && state.get(FACING_TO_PROPERTY_MAP.get(direction)))
                    connections++;
            if (connections > 2)
                return true;
        }
        return false;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, BlockStateProperties.WATERLOGGED);
        super.fillStateContainer(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return updateBlockState(getDefaultState(), context.getNearestLookingDirection(), null, context.getWorld(),
                context.getPos()).with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbourState,
                                          IWorld world, BlockPos pos, BlockPos neighbourPos) {
        if (state.get(BlockStateProperties.WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return updateBlockState(state, direction, direction.getOpposite(), world, pos);
    }

    public BlockState updateBlockState(BlockState state, Direction preferredDirection, @Nullable Direction ignore,
                                       ILightReader world, BlockPos pos) {
        // Update sides that are not ignored
        for (Direction d : Iterate.directions)
            if (d != ignore)
                state = state.with(FACING_TO_PROPERTY_MAP.get(d),
                        canConnectTo(world, pos.offset(d), world.getBlockState(pos.offset(d)), d.getOpposite()));

        // See if it has enough connections
        Direction connectedDirection = null;
        for (Direction d : Iterate.directions) {
            if (state.get(FACING_TO_PROPERTY_MAP.get(d))) {
                if (connectedDirection != null)
                    return state;
                connectedDirection = d;
            }
        }

        // Add opposite end if only one connection
        if (connectedDirection != null)
            return state.with(FACING_TO_PROPERTY_MAP.get(connectedDirection.getOpposite()), true);

        // Use preferred
        return state.with(FACING_TO_PROPERTY_MAP.get(preferredDirection), true)
                .with(FACING_TO_PROPERTY_MAP.get(preferredDirection.getOpposite()), true);
    }

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isRemote)
			return;
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof HeatPipeTileEntity)
			((HeatPipeTileEntity) te).updateConnection();
	}
	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new HeatPipeTileEntity();
	}
	
}
