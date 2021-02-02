package com.sturdycobble.createrevision.contents.heat;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.sturdycobble.createrevision.api.heat.HeatRecipe;
import com.sturdycobble.createrevision.contents.heat.HeatExchangerTileEntity.HeatExchangerInventory;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerRecipe extends HeatRecipe<HeatExchangerInventory>{

	public HeatExchangerRecipe(ResourceLocation idIn, NonNullList<Ingredient> ingredientsIn,
			NonNullList<ProcessingOutput> resultsIn, @Nullable NonNullList<FluidIngredient> fluidIngredientsIn, @Nullable NonNullList<FluidStack> fluidResultsIn, 
			float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		super(ModRecipeTypes.HEAT_EXCHANGER, idIn, ingredientsIn, resultsIn, fluidIngredientsIn, fluidResultsIn, heatIn, tempMinIn, tempMaxIn, durationIn);
	}
	
	public HeatExchangerRecipe(ResourceLocation idIn, NonNullList<Ingredient> ingredientsIn,
			NonNullList<ProcessingOutput> resultsIn, float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		super(ModRecipeTypes.HEAT_EXCHANGER, idIn, ingredientsIn, resultsIn, heatIn, tempMinIn, tempMaxIn, durationIn);
	}

	@Override
	public boolean matches(HeatExchangerInventory inv, World worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
			.test(inv.getStackInSlot(0));
	}

}