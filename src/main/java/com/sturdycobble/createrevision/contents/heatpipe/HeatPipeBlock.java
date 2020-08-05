package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
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
		TileEntity te = reader.getTileEntity(pos);
		return (int) te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null).getHeatEnergy() / 20;
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rayTraceResult) {
		TileEntity te = world.getTileEntity(pos);
		if (hand == Hand.MAIN_HAND)
			te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null).applyHeat(100);
		else
			te.getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null).applyHeat(-100);
		return ActionResultType.SUCCESS;
	}

}
