package com.sturdycobble.createrevision.contents.geo.bedrock;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class BedrockAnvilBlock extends KineticBlock implements ITE<BedrockAnvilTileEntity> {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public BedrockAnvilBlock(final Properties properties) {
		super(properties);
		BlockState defaultState = this.stateContainer.getBaseState().with(FACING, Direction.NORTH);
		this.setDefaultState(defaultState);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		super.fillStateContainer(builder);
	}

	public Direction getPreferredFacing(BlockItemUseContext context) {
		Direction prefferedSide = null;
		for (Direction side : Direction.values()) {
			BlockState blockState = context.getWorld().getBlockState(context.getPos().offset(side));
			if (blockState.getBlock() instanceof IRotate) {
				if (((IRotate) blockState.getBlock()).hasShaftTowards(context.getWorld(), context.getPos().offset(side),
						blockState, side.getOpposite()))
					if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
						prefferedSide = null;
						break;
					} else {
						prefferedSide = side;
					}
			}
		}
		return prefferedSide;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction preferred = context.getPlacementHorizontalFacing();
		if (preferred == null || (context.getPlayer() != null && context.getPlayer().isSneaking())) {
			Direction nearestLookingDirection = context.getNearestLookingDirection();
			return getDefaultState().with(FACING, context.getPlayer()
					.isSneaking() ? nearestLookingDirection : nearestLookingDirection.getOpposite());
		}
		return getDefaultState().with(FACING, preferred.getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AllShapes.CASING_12PX.get(state.get(FACING));
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING).getOpposite();
	}

	@Override
	public TileEntity createTileEntity(BlockState paramBlockState, IBlockReader paramIBlockReader) {
		return new BedrockAnvilTileEntity();
	}

	@Override
	protected boolean hasStaticPart() {
		return true;
	}

	@Override
	public Class<BedrockAnvilTileEntity> getTileEntityClass() {
		return BedrockAnvilTileEntity.class;
	}

}
