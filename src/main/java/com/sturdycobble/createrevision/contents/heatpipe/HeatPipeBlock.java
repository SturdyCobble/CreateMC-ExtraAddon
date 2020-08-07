package com.sturdycobble.createrevision.contents.heatpipe;

import com.sturdycobble.createrevision.contents.heatsystem.CapabilityHeat;
import com.sturdycobble.createrevision.contents.heatsystem.HeatContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class HeatPipeBlock extends Block {
	public HeatPipeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		System.out.println("placed");
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof HeatPipeTileEntity) {
			LazyOptional<HeatContainer> cap = world.getTileEntity(pos).getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
			if (cap.isPresent())
				System.out.println("temp : " + cap.orElse(null).getTemp());
		}
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new HeatPipeTileEntity();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		System.out.println("providing");
		return true;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader reader, BlockPos pos, Direction direction) {
		System.out.println("unlimited power!!!!");
		if (reader.getTileEntity(pos) != null) {
			HeatContainer cap = reader.getTileEntity(pos).getCapability(CapabilityHeat.HEAT_CAPABILITY, null).orElse(null);
			if (cap != null) {
				System.out.println("power : " + cap.getTemp() / 40);
				return (int) cap.getTemp() / 40;
			}
		}
		return 15;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader reader, BlockPos pos, Direction direction) {
		return this.getWeakPower(state, reader, pos, direction);
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public ActionResultType onUse(BlockState state, World world, BlockPos pos, PlayerEntity entity, Hand hand, BlockRayTraceResult rayTraceResult) {
		LazyOptional<HeatContainer> cap = world.getTileEntity(pos).getCapability(CapabilityHeat.HEAT_CAPABILITY, null);
		if (cap.isPresent()) {
			if (hand == Hand.MAIN_HAND)
				cap.orElse(null).applyHeat(100);
			else
				cap.orElse(null).applyHeat(-100);
			System.out.println(cap.orElse(null).getTemp());
		}
		return ActionResultType.SUCCESS;
	}

}
