package com.sturdycobble.createrevision.contents.heat;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.VoxelShaper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import com.sturdycobble.createrevision.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HeatEngineBlock extends EngineBlock implements ITE<HeatEngineTileEntity> {

	private static final VoxelShaper HEAT_ENGINE_SHAPER = VoxelShaper.forDirectional(
			Block.makeCuboidShape(0.1D, 0.1D, 0.1D, 15.9D, 15.9D, 15.9D), Direction.UP);

	public HeatEngineBlock(Properties builder) {
		super(builder);
	}

	@Override
	public TileEntity createTileEntity(BlockState arg0, IBlockReader arg1) {
		return new HeatEngineTileEntity();
	}

	@Override
	public AllBlockPartials getFrameModel() {
		return AllBlockPartials.FURNACE_GENERATOR_FRAME;
	}

	@Override
	protected boolean isValidBaseBlock(BlockState baseBlock, IBlockReader world, BlockPos pos) {
		return baseBlock.getBlock() == ModBlocks.HEAT_EXCHANGER.get();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		if (worldIn instanceof WrappedWorld)
			return;
		if (worldIn.isRemote)
			return;

		withTileEntityDo(worldIn, pos, HeatEngineTileEntity::updateAdjacentHeatContainers);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		withTileEntityDo(worldIn, pos, HeatEngineTileEntity::updateAdjacentHeatContainers);
	}

	@Override
	public Class<HeatEngineTileEntity> getTileEntityClass() {
		return HeatEngineTileEntity.class;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return HEAT_ENGINE_SHAPER.get(state.get(HORIZONTAL_FACING));
	}

}
