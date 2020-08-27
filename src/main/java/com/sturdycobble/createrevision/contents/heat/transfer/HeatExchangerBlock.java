package com.sturdycobble.createrevision.contents.heat.transfer;

import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HeatExchangerBlock extends Block implements ITE<HeatExchangerTileEntity> {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public HeatExchangerBlock(Properties properties) {
		super(properties);
		BlockState defaultState = this.stateContainer.getBaseState().with(FACING, Direction.NORTH);
		this.setDefaultState(defaultState);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		super.fillStateContainer(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
			Direction preferredFacing = context.getNearestLookingDirection();
		return getDefaultState().with(FACING, context.getPlayer() != 
				null && context.getPlayer().isSneaking() ? preferredFacing.getOpposite() : preferredFacing);
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity())) {
			world.removeTileEntity(pos);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState iBlockState) {
		return BlockRenderType.MODEL;
	}
	
	protected void blockUpdate(BlockState state, World world, BlockPos pos) {
		if (world instanceof WrappedWorld || world.isRemote)
			return;
		withTileEntityDo(world, pos, te -> te.updateConnection());
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		blockUpdate(state, world, pos);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		blockUpdate(state, world, pos);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader blockReader) {
		return new HeatExchangerTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public Class<HeatExchangerTileEntity> getTileEntityClass() {
		return HeatExchangerTileEntity.class;
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
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return  VoxelShaper.forDirectional(Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Direction.UP).get(state.get(FACING));
	}

}
