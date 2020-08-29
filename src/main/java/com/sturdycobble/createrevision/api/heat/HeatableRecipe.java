package com.sturdycobble.createrevision.api.heat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
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

public abstract class HeatableRecipe<T extends IInventory> implements IRecipe<T> {

	protected final List<ProcessingIngredient> ingredients;
	protected final ResourceLocation id;
	protected final String group;
	protected final int processingDuration;
	protected final List<FluidStack> fluidIngredients;
	protected final List<FluidStack> fluidResults;
	protected final float heatProduction;
	protected final float tempMin;
	protected final float tempMax;
	private final List<ProcessingOutput> results;
	private final IRecipeType<?> type;
	private final IRecipeSerializer<?> serializer;
	
	public HeatableRecipe(ModRecipeTypes typeIn, ResourceLocation idIn, String groupIn, List<ProcessingIngredient> ingredientsIn,
			List<ProcessingOutput> resultsIn, @Nullable List<FluidStack> fluidIngredientsIn, @Nullable List<FluidStack> fluidResultsIn, 
			float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		type = typeIn.type;
		serializer = typeIn.serializer;
		id = idIn;
		group = groupIn;
		ingredients = ingredientsIn;
		results = resultsIn;
		processingDuration = durationIn;
		fluidIngredients = fluidIngredientsIn;
		fluidResults = fluidResultsIn;
		heatProduction = heatIn;
		tempMin = tempMinIn;
		tempMax = tempMaxIn;
	}
	
	public HeatableRecipe(ModRecipeTypes typeIn, ResourceLocation idIn, String groupIn, List<ProcessingIngredient> ingredientsIn,
			List<ProcessingOutput> resultsIn, float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		type = typeIn.type;
		serializer = typeIn.serializer;
		id = idIn;
		group = groupIn;
		ingredients = ingredientsIn;
		results = resultsIn;
		processingDuration = durationIn;
		fluidIngredients = null;
		fluidResults = null;
		heatProduction = heatIn;
		tempMin = tempMinIn;
		tempMax = tempMaxIn;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		this.ingredients.forEach(ingr -> nonnulllist.add(ingr.getIngredient()));
		return nonnulllist;
	}

	public int getProcessingDuration() {
		return processingDuration;
	}

	public List<ItemStack> rollResults() {
		List<ItemStack> results = new ArrayList<ItemStack>();
		for (ProcessingOutput output : getRollableItemResults()) {
			ItemStack stack = output.rollOutput();
			if (!stack.isEmpty())
				results.add(stack);
		}
		return results;
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
		return getRollableItemResults().isEmpty() ? ItemStack.EMPTY : getRollableItemResults().get(0).getStack();
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
		return group;
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}

	protected int getMaxInputCount() {
		return 1;
	}

	protected int getMaxOutputCount() {
		return 15;
	}

	protected boolean canHaveCatalysts() {
		return false;
	}

	public List<ProcessingOutput> getRollableItemResults() {
		return results;
	}

	public List<ProcessingIngredient> getRollableIngredients() {
		return ingredients;
	}
	
	public double getHeatProduction() {
		return heatProduction;
	}
	
	public Tuple<Float, Float> getTempRange() {
		return new Tuple<Float, Float>(tempMin, tempMax);
	}

	public List<ItemStack> getPossibleOutputs() {
		return getRollableItemResults().stream().map(ProcessingOutput::getStack).collect(Collectors.toList());
	}

	protected boolean canHaveFluidIngredient() {
		return false;
	}

	protected boolean canHaveFluidOutput() {
		return false;
	}
	
}