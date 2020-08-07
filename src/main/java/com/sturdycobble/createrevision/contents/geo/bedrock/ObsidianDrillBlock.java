package com.sturdycobble.createrevision.contents.geo.bedrock;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

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

public class ObsidianDrillBlock extends DirectionalKineticBlock implements ITE<ObsidianDrillTileEntity> {
	
	public ObsidianDrillBlock(final Properties properties) {
		super(properties);
		BlockState defaultState = this.stateContainer.getBaseState().with(FACING, Direction.NORTH);
		this.setDefaultState(defaultState);
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
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity())) {
			withTileEntityDo(world, pos, te -> {
				ItemHelper.dropContents(world, pos, te.inventory);
			});
			
			world.removeTileEntity(pos);
		}
	}
	

	@Override
	public BlockRenderType getRenderType(BlockState iBlockState) {
		return BlockRenderType.MODEL;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING).getOpposite();
	}
	
	protected void blockUpdate(BlockState state, World world, BlockPos pos) {
		if (world instanceof WrappedWorld || world.isRemote)
			return;
		withTileEntityDo(world, pos, te -> te.updateDrill(state.get(FACING)));
	}
	
	@Override
	public BlockState updateAfterWrenched(BlockState newState, ItemUseContext context) {
		blockUpdate(newState, context.getWorld(), context.getPos());
		return newState;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		blockUpdate(state, worldIn, pos);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		blockUpdate(state, world, pos);
	}

	@Override
	public boolean showCapacityWithAnnotation() {
		return true;
	}
	
	@Override
	public Class<ObsidianDrillTileEntity> getTileEntityClass() {
		return ObsidianDrillTileEntity.class;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader paramIBlockReader) {
		return ModTileEntityTypes.OBSIDIAN_DRILL.get().create();
	}

	@Override
	protected boolean hasStaticPart() {
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return AllShapes.CASING_12PX.get(state.get(FACING));
	}

}
