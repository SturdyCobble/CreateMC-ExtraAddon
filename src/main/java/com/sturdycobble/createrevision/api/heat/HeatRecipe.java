package com.sturdycobble.createrevision.api.heat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fluids.FluidStack;

public abstract class HeatRecipe<T extends IInventory> implements IRecipe<T> {

	protected ResourceLocation id;
	protected NonNullList<Ingredient> ingredients;
	protected NonNullList<ProcessingOutput> results;
	protected NonNullList<FluidIngredient> fluidIngredients;
	protected NonNullList<FluidStack> fluidResults;
	protected int processingDuration;
	protected final float heatProduction;
	protected final float tempMin;
	protected final float tempMax;
	private final IRecipeType<?> type;
	private final IRecipeSerializer<?> serializer;
	
	public HeatRecipe(ModRecipeTypes typeIn, ResourceLocation idIn, NonNullList<Ingredient> ingredientsIn,
			NonNullList<ProcessingOutput> resultsIn, @Nullable NonNullList<FluidIngredient> fluidIngredientsIn, @Nullable NonNullList<FluidStack> fluidResultsIn, 
			float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		type = typeIn.type;
		serializer = typeIn.serializer;
		id = idIn;
		ingredients = ingredientsIn;
		results = resultsIn;
		processingDuration = durationIn;
		fluidIngredients = fluidIngredientsIn;
		fluidResults = fluidResultsIn;
		heatProduction = heatIn;
		tempMin = tempMinIn;
		tempMax = tempMaxIn;
	}
	
	public HeatRecipe(ModRecipeTypes typeIn, ResourceLocation idIn, NonNullList<Ingredient> ingredientsIn,
			NonNullList<ProcessingOutput> resultsIn, float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		type = typeIn.type;
		serializer = typeIn.serializer;
		id = idIn;
		ingredients = ingredientsIn;
		results = resultsIn;
		processingDuration = durationIn;
		fluidIngredients = null;
		fluidResults = null;
		heatProduction = heatIn;
		tempMin = tempMinIn;
		tempMax = tempMaxIn;
	}

	protected abstract int getMaxInputCount();

	protected abstract int getMaxOutputCount();

	protected boolean canRequireHeat() {
		return false;
	}

	protected boolean canSpecifyDuration() {
		return true;
	}

	protected int getMaxFluidInputCount() {
		return 0;
	}

	protected int getMaxFluidOutputCount() {
		return 0;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}
	
	public NonNullList<FluidIngredient> getFluidIngredients() {
		return fluidIngredients;
	}
	
	public NonNullList<ProcessingOutput> getRollableResults() {
		return results;
	}
	
	public NonNullList<FluidStack> getFluidResults() {
		return fluidResults;
	}

	public List<ItemStack> getRollableResultsAsItemStacks() {
		return getRollableResults().stream()
			.map(ProcessingOutput::getStack)
			.collect(Collectors.toList());
	}

	public List<ItemStack> rollResults() {
		List<ItemStack> results = new ArrayList<>();
		for (ProcessingOutput output : getRollableResults()) {
			ItemStack stack = output.rollOutput();
			if (!stack.isEmpty())
				results.add(stack);
		}
		return results;
	}

	public int getProcessingDuration() {
		return processingDuration;
	}
	
	@Override
	public ItemStack getCraftingResult(T inv) {
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return getRollableResults().isEmpty() ? ItemStack.EMPTY
			: getRollableResults().get(0)
				.getStack();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return serializer;
	}

	@Override
	public String getGroup() {
		return "thermal";
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}
	
	public double getHeatProduction() {
		return heatProduction;
	}
	
	public Tuple<Float, Float> getTempRange() {
		return new Tuple<Float, Float>(tempMin, tempMax);
	}
	
}