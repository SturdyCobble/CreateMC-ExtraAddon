package sturdycobble.createrevision.contents.reinforced_depot;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.api.depot_recipe.SimpleBeaconRecipe;
import sturdycobble.createrevision.init.ModRecipeTypes;
import sturdycobble.createrevision.utils.RGBColor;

public class BeaconDepotRecipe extends SimpleBeaconRecipe<Container> {

    public BeaconDepotRecipe(ResourceLocation recipeId, NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results,
                             int power, RGBColor color) {
        this.id = recipeId;
        this.ingredients = ingredients;
        this.results = results;
        this.power = power;
        this.color = color;
    }

    public RGBColor getColor() {
        return color;
    }

    public int getPower() {
        return power;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEACON_DEPOT_RECIPE.serializer;
    }

    @Override
    public RecipeType getType() {
        return ModRecipeTypes.BEACON_DEPOT_RECIPE.getType();
    }

    @Override
    public boolean matches(Container container, Level world, int beaconLevel, RGBColor beaconColor) {
        if (container.isEmpty() || beaconLevel < power)
            return false;
        if (RGBColor.squareDistance(color, beaconColor) < 0.27) {
            Ingredient ingredient0 = Ingredient.EMPTY;
            if (ingredients.size() > 0) {
                ingredient0 = ingredients.get(0);
            }
            return ingredient0.test(container.getItem(0));
        }
        return false;
    }

}
