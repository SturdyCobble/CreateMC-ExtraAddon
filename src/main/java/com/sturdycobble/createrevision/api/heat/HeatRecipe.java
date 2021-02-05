package com.sturdycobble.createrevision.api.heat;

import java.util.List;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public interface HeatRecipe<C extends IInventory> extends IRecipe<C> {

	int getDuration();

	double getHeatProduction();

	double getTempMin();

	double getTempMax();

	NonNullList<Ingredient> getIngredients();

	NonNullList<FluidIngredient> getFluidIngredients();

	NonNullList<ProcessingOutput> getRollableResults();

	NonNullList<FluidStack> getFluidResults();

	default boolean isValidTemp(double temp) {
		return getTempMin() < temp && temp < getTempMax();
	}

}