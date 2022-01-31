package sturdycobble.createrevision.api.depot_recipe;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.utils.ColorCondition;
import sturdycobble.createrevision.utils.RGBColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SimpleBeaconRecipe<C extends Container> implements Recipe<C>, BeaconRecipe<C> {

    protected ResourceLocation id;
    protected NonNullList<Ingredient> ingredients;
    protected NonNullList<ProcessingOutput> results;
    protected int power;
    protected ColorCondition colorCondition;

    boolean matches(C container, Level world, int beaconLevel, DyeColor color) {
        return matches(container, world, beaconLevel, new RGBColor(color));
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

    public List<ItemStack> getRollableResultsAsItemStacks() {
        return getRollableResults().stream()
                .map(ProcessingOutput::getStack)
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(C container, Level world) {
        if (container.isEmpty())
            return false;
        return ingredients.get(0).test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(C inv) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ItemStack getResultItem() {
        return getRollableResults().isEmpty() ? ItemStack.EMPTY : getRollableResults().get(0).getStack();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

}
