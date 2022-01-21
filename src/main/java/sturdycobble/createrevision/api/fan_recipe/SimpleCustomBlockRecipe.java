package sturdycobble.createrevision.api.fan_recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import sturdycobble.createrevision.api.fan_recipe.CustomFluidOrBlockRecipe;

public abstract class SimpleCustomBlockRecipe<C extends Container> implements Recipe<C>, CustomFluidOrBlockRecipe<C> {

}
