package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HeatPipeBlock extends Block {
	public HeatPipeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new HeatPipeTileEntity();
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader reader, BlockPos pos, Direction direction) {
		return (int)Utils.getHeatContainer(reader.getTileEntity(pos)).getHeatEnergy()/20;
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rayTraceResult) {
		if(hand == Hand.MAIN_HAND)
			Utils.getHeatContainer(Utils.getTileEntity(world, pos)).applyHeat(100);
		else
			Utils.getHeatContainer(Utils.getTileEntity(world, pos)).applyHeat(-100);
		return ActionResultType.SUCCESS;
	}

}
