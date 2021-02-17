package com.sturdycobble.createrevision.contents.heat;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class FrictionHeaterBlock extends DirectionalKineticBlock implements ITE<FrictionHeaterTileEntity> {
	
	private static final VoxelShaper FRICTION_HEATER_SHAPER = VoxelShaper.forDirectional(
			Block.makeCuboidShape(0.0D, 0.1D, 0.0D, 16.0D, 14.0D, 16.0D), Direction.UP);

	public FrictionHeaterBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction preferredFacing = getPreferredFacing(context);
		if (preferredFacing == null)
			preferredFacing = context.getNearestLookingDirection();
		return getDefaultState().with(FACING, context.getPlayer() != 
				null && context.getPlayer().isSneaking() ? preferredFacing : preferredFacing.getOpposite());
	}

	@Override
	public BlockRenderType getRenderType(BlockState iBlockState) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING).getOpposite();
	}
	
	private void blockUpdate(BlockState state, World world, BlockPos pos) {
		if (world instanceof WrappedWorld || world.isRemote)
			return;
		withTileEntityDo(world, pos, te -> te.updateAllNeighbors(world.getBlockState(pos)));
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
	public BlockState updateAfterWrenched(BlockState newState, ItemUseContext context) {
		blockUpdate(newState, context.getWorld(), context.getPos());
		return newState;
	}

	@Override
	public boolean showCapacityWithAnnotation() {
		return true;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING).getAxis();
	}

	@Override
	public Class<FrictionHeaterTileEntity> getTileEntityClass() {
		return FrictionHeaterTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader blockReader) {
		return new FrictionHeaterTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return FRICTION_HEATER_SHAPER.get(state.get(FACING));
	}
	
}
