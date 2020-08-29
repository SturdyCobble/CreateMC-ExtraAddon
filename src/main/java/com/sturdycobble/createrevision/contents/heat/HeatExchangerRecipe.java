package com.sturdycobble.createrevision.contents.heat;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.contraptions.processing.ProcessingIngredient;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.sturdycobble.createrevision.api.heat.HeatableRecipe;
import com.sturdycobble.createrevision.contents.heat.HeatExchangerRecipe.HeatExchangerInventory;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@ParametersAreNonnullByDefault
public class HeatExchangerRecipe extends HeatableRecipe<HeatExchangerInventory>{

	public HeatExchangerRecipe(ResourceLocation idIn, String groupIn, List<ProcessingIngredient> ingredientsIn,
			List<ProcessingOutput> resultsIn, @Nullable List<FluidStack> fluidIngredients, 
			@Nullable List<FluidStack> fluidResults, float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		super(ModRecipeTypes.HEAT_EXCHANGER, idIn, groupIn, ingredientsIn, resultsIn, heatIn, tempMinIn, tempMaxIn, durationIn);
	}
	
	public HeatExchangerRecipe(ResourceLocation idIn, String groupIn, List<ProcessingIngredient> ingredientsIn,
			List<ProcessingOutput> resultsIn, float heatIn, float tempMinIn, float tempMaxIn, int durationIn) {
		super(ModRecipeTypes.HEAT_EXCHANGER, idIn, groupIn, ingredientsIn, resultsIn, heatIn, tempMinIn, tempMaxIn, durationIn);
	}

	@Override
	public boolean matches(HeatExchangerInventory inv, World worldIn) {
		if (inv.isEmpty())
			return false;
		return ingredients.get(0)
			.test(inv.getStackInSlot(0));
	}

	@Override
	protected int getMaxOutputCount() {
		return 4;
	}
	class HeatExchangerInventory extends RecipeWrapper {
		public HeatExchangerInventory() {
			super(new ItemStackHandler(1));
		}
	}

}