package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.recipe.RecipeConditions;
import com.simibubi.create.foundation.utility.recipe.RecipeFinder;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.IHeatableTileEntity;
import com.sturdycobble.createrevision.api.heat.SimpleHeatContainer;
import com.sturdycobble.createrevision.init.ModBlocks;
import com.sturdycobble.createrevision.init.ModRecipeTypes;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;
import com.sturdycobble.createrevision.utils.HeatUtils;
import com.sturdycobble.createrevision.utils.HeatUtils.FacingDistance;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatExchangerTileEntity extends TileEntity implements IHeatableTileEntity, ITickableTileEntity {

	public boolean checkConnection;

	private static final Object heatRecipesKey = new Object();
	
	private double heatCapacity;
	private double conductivity;
	private Map<IHeatableTileEntity, FacingDistance> neighborMap;
	private double heatExchanged;
	private int exchangeTime;

	public HeatExchangerTileEntity() {
		super(ModTileEntityTypes.HEAT_EXCHANGER.get());
		neighborMap = new HashMap<IHeatableTileEntity, FacingDistance>();
		heatCapacity = 3;
		conductivity = 0.7;
		checkConnection = true;
		heatExchanged = 0;
		exchangeTime = 0;
	}

	private LazyOptional<HeatContainer> heatContainer = LazyOptional.of(() -> new SimpleHeatContainer(300, heatCapacity));

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return heatContainer.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (checkConnection = true) {
			updateConnection();
			checkConnection = false;
		}

		if (world.getWorldInfo().getGameTime() % 5 == 0) {
			double temp = this.heatContainer.orElse(null).getTemp();
			double heatCurrent = HeatUtils.getHeatCurrent(neighborMap, temp, conductivity);

			setPowerWithInteraction(temp);
			double power = 0;
			if (exchangeTime > 0) {
				power = heatExchanged;
				exchangeTime--;
			}

			temp += (heatCurrent + power) / heatCapacity;
			this.heatContainer.orElse(null).setTemp(temp);
		}
	}

	public void setPowerWithInteraction(double temp) {
		Direction facing = world.getBlockState(pos).get(FACING);
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

	@Override
	public void markConnection() {
		checkConnection = true;
	}

	@Override
	public Map<IHeatableTileEntity, FacingDistance> findNeighborNode() {
		Map<IHeatableTileEntity, FacingDistance> nodes = new HashMap<IHeatableTileEntity, FacingDistance>();
		for (Direction direction : Direction.values()) {
			if (world.getBlockState(pos.offset(direction)).getBlock() == ModBlocks.HEAT_PIPE.get())
				nodes.put((IHeatableTileEntity) world.getTileEntity(pos.offset(direction)), new FacingDistance(direction, 1L));
		}
		return nodes;
	}

	@Override
	public void updateConnection() {
		neighborMap = findNeighborNode();
		for (IHeatableTileEntity neighbor : neighborMap.keySet())
			neighbor.markConnection();
	}

	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public Map<IHeatableTileEntity, FacingDistance> getNeighborMap() {
		return neighborMap;
	}

}
