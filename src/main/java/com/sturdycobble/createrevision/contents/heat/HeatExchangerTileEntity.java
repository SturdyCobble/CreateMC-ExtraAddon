package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.utility.recipe.RecipeConditions;
import com.simibubi.create.foundation.utility.recipe.RecipeFinder;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModRecipeTypes;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class HeatExchangerTileEntity extends SyncedTileEntity implements ITickableTileEntity {

	private static final Object heatRecipesKey = new Object();
	
	private final double conductivity = ModConfigs.getHeatPipeConductivity();
	private double heatExchanged = 0;
	private int exchangeTime = 0;

	public HeatExchangerTileEntity() {
		super(ModTileEntityTypes.HEAT_EXCHANGER.get());
	}
	
	private final SimpleHeatContainer heatContainer = new SimpleHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getHeatPipeHeatCapacity();
		}
	};

	private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			Direction facing = getBlockState().get(FACING).getOpposite();
			if (side != facing)
				return heatContainerCap.cast();
			else return LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % 3 == 0) {
			double temp = heatContainer.getTemp();
			double heatCurrent = HeatUtils.getHeatCurrent(world, pos, heatContainer.getNeighbors(), temp, conductivity);

			setPowerWithInteraction(temp);
			double power = 0;
			if (exchangeTime > 0) {
				power = heatExchanged;
				exchangeTime--;
			}

			temp += (heatCurrent + power) / heatContainer.getCapacity();
			heatContainer.setTemp(temp);
			markDirty();
		}
	}

	public void setPowerWithInteraction(double temp) {
		Direction facing = getBlockState().get(FACING);
		BlockPos offsetPos = pos.offset(facing.getOpposite());
		Block offsetBlock = world.getBlockState(offsetPos).getBlock();
		ItemStack itemStack = new ItemStack(offsetBlock.asItem());
		List<? extends IRecipe<?>> recipes = getRecipes(itemStack);
	
		if (recipes.isEmpty())
			return;
		for (IRecipe<?> recipe : recipes) {
			HeatExchangerRecipe heatRecipe = (HeatExchangerRecipe) recipe;
			if (heatRecipe.getTempRange().getA() < temp && temp < heatRecipe.getTempRange().getB()) {
				Block resultBlock = Block.getBlockFromItem(heatRecipe.rollResults().get(0).getItem());
				world.setBlockState(offsetPos, resultBlock.getDefaultState());
				heatExchanged = heatRecipe.getHeatProduction();
				exchangeTime = heatRecipe.getProcessingDuration();
				break;
			}	
		}
	}
	
	private List<? extends IRecipe<?>> getRecipes(ItemStack itemStack) {
		List<IRecipe<?>> startedSearch = RecipeFinder.get(heatRecipesKey, world,
			RecipeConditions.isOfType(ModRecipeTypes.HEAT_EXCHANGER.getType()));
		return startedSearch.stream()
			.filter(RecipeConditions.firstIngredientMatches(itemStack))
			.collect(Collectors.toList());
	}
	
	public boolean isValidDirection(@Nonnull Direction direction, Direction facing) {
		if (direction != facing.getOpposite())
			return false;
		return true;
	}
	
	public void updateAllNeighbors(BlockState state) {
		Direction facing = state.get(FACING);
		for (Direction direction : Direction.values()) {
			if (isValidDirection(direction, facing)) {
				TileEntity te = world.getTileEntity(pos.offset(direction));
				if (te != null) {
					LazyOptional<HeatContainer> neighborContainer = te.getCapability(CapabilityHeat.HEAT_CAPABILITY, direction.getOpposite());
					if (neighborContainer.isPresent())
						heatContainer.putNeighbor(direction, 1);
					else
						heatContainer.removeNeighbor(direction);
				}
			}
		}
	}
	
	@Override
    public CompoundNBT write(CompoundNBT tag) {
		tag = heatContainer.serializeNBT();
        return super.write(tag);
    }
    
	@Override
	public void read(CompoundNBT tag) {
		heatContainer.deserializeNBT(tag);
		super.read(tag);
	}
	
	class HeatExchangerInventory extends RecipeWrapper {
		public HeatExchangerInventory() {
			super(new ItemStackHandler(1));
		}
	}

}
