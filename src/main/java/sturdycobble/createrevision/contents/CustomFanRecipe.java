package sturdycobble.createrevision.contents;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.api.fan_recipe.SimpleCustomBlockRecipe;
import sturdycobble.createrevision.init.ModRecipeTypes;
import sturdycobble.createrevision.utils.FluidOrBlock;

import java.util.ArrayList;
import java.util.List;

public class CustomFanRecipe<C extends Container> extends SimpleCustomBlockRecipe<C> {

    protected FluidOrBlock sourceType;
    protected ResourceLocation id;
    protected NonNullList<Ingredient> ingredients;
    protected NonNullList<ProcessingOutput> results;

    public CustomFanRecipe(ResourceLocation recipeId, NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results,
                           FluidOrBlock sourceType) {
        this.ingredients = ingredients;
        this.results = results;
        this.id = recipeId;
        this.sourceType = sourceType;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public NonNullList<ProcessingOutput> getRollableResults() {
        return results;
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

    @Override
    public boolean matches(C container, Level world, FluidOrBlock type) {
        if (container.isEmpty())
            return false;
        Ingredient ingredient0 = Ingredient.EMPTY;
        if (ingredients.size() > 0) {
            ingredient0 = ingredients.get(0);
        }
        return ingredient0.test(container.getItem(0)) && type.equals(this.sourceType);
    }

    @Override
    public boolean matches(C container, Level world) {
        if (container.isEmpty())
            return false;
        return ingredients.get(0).test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(C container) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return getRollableResults().isEmpty() ? ItemStack.EMPTY : getRollableResults().get(0).getStack();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CUSTOM_FAN_RECIPE.serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CUSTOM_FAN_RECIPE.getType();
    }

}
