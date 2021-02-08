package com.sturdycobble.createrevision.contents.heat;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.sturdycobble.createrevision.api.heat.HeatProcessingRecipe;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FrictionRecipe extends HeatProcessingRecipe<RecipeWrapper> {

	public FrictionRecipe(ResourceLocation recipeId, NonNullList<Ingredient> ingredients,
			NonNullList<ProcessingOutput> results, NonNullList<FluidIngredient> fluidIngredients,
			NonNullList<FluidStack> fluidResults, float heatProduction, float tempMin, float tempMax,
			int processingDuration) {
		super(ModRecipeTypes.FRICTION, recipeId, ingredients, results, fluidIngredients, fluidResults, heatProduction,
				tempMin, tempMax, processingDuration);
	}

	@Override
	public boolean matches(RecipeWrapper inv, World worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0).test(inv.getStackInSlot(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 2;
	}

}
