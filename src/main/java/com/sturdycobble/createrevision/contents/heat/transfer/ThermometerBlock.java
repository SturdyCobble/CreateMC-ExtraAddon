package com.sturdycobble.createrevision.contents.heat.transfer;

import com.simibubi.create.foundation.utility.VoxelShaper;
import com.sturdycobble.createrevision.contents.heat.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heat.HeatContainer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class ThermometerBlock extends Block{
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public ThermometerBlock(Properties properties) {
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
				null && context.getPlayer().isSneaking() ? preferredFacing : preferredFacing.getOpposite());
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
		Direction facing = world.getBlockState(pos).get(FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (te != null) {
			LazyOptional<HeatContainer> heatContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
			if (!world.isRemote && hand == Hand.MAIN_HAND && heatContainer.isPresent()) {
				double temp = heatContainer.orElse(null).getTemp();
				player.sendMessage(new TranslationTextComponent("TEMP : "+Math.floor(temp)));
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState iBlockState) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return  VoxelShaper.forDirectional(Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 2.0D, 13.0D), Direction.UP).get(state.get(FACING));
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
}
