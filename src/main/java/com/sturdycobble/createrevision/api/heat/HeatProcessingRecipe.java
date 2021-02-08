package com.sturdycobble.createrevision.api.heat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public abstract class HeatProcessingRecipe<T extends IInventory> implements HeatRecipe<T> {

	protected ResourceLocation id;
	protected NonNullList<Ingredient> ingredients;
	protected NonNullList<ProcessingOutput> results;
	protected NonNullList<FluidIngredient> fluidIngredients;
	protected NonNullList<FluidStack> fluidResults;
	protected int processingDuration;

	protected final float heatProduction;
	protected final float tempMin;
	protected final float tempMax;

	private IRecipeType<?> type;
	private IRecipeSerializer<?> serializer;
	private ModRecipeTypes enumType;

	public HeatProcessingRecipe(ModRecipeTypes recipeType, ResourceLocation recipeId,
			NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results,
			NonNullList<FluidIngredient> fluidIngredients, NonNullList<FluidStack> fluidResults, float heatProduction,
			float tempMin, float tempMax, int processingDuration) {
		this.enumType = recipeType;
		this.processingDuration = processingDuration;
		this.fluidIngredients = fluidIngredients;
		this.fluidResults = fluidResults;
		this.serializer = recipeType.serializer;
		this.ingredients = ingredients;
		this.type = recipeType.type;
		this.results = results;
		this.id = recipeId;
		this.heatProduction = heatProduction;
		this.tempMin = tempMin;
		this.tempMax = tempMax;
	}

	protected abstract int getMaxInputCount();

	protected abstract int getMaxOutputCount();

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
		return getRollableResults().stream().map(ProcessingOutput::getStack).collect(Collectors.toList());
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

	// IRecipe<> paperwork

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
		return getRollableResults().isEmpty() ? ItemStack.EMPTY : getRollableResults().get(0).getStack();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return serializer;
	}

	// Processing recipes do not show up in the recipe book
	@Override
	public String getGroup() {
		return "thermal";
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}

	// Additional Data added by subtypes

	public void readAdditional(JsonObject json) {
	}

	public void readAdditional(PacketBuffer buffer) {
	}

	public void writeAdditional(JsonObject json) {
	}

	public void writeAdditional(PacketBuffer buffer) {
	}

	public ModRecipeTypes getEnumType() {
		return enumType;
	}

	@Override
	public int getDuration() {
		return processingDuration;
	}

	@Override
	public double getHeatProduction() {
		return heatProduction;
	}

	@Override
	public double getTempMin() {
		return tempMin;
	}

	@Override
	public double getTempMax() {
		return tempMax;
	}

}
