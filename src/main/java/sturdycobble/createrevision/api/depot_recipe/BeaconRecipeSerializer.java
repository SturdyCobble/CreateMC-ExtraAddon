package sturdycobble.createrevision.api.depot_recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import sturdycobble.createrevision.utils.RGBColor;

public class BeaconRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SimpleBeaconRecipe<?>> {

    protected final BeaconRecipeSerializer.IRecipeFactory factory;

    public BeaconRecipeSerializer(BeaconRecipeSerializer.IRecipeFactory factoryIn) {
        factory = factoryIn;
    }

    @Override
    public SimpleBeaconRecipe<?> fromJson(ResourceLocation recipeId, JsonObject json) {
        int power = 0;

        if (GsonHelper.isValidNode(json, "power")) {
            power = GsonHelper.getAsInt(json, "power");
        }

        RGBColor color = RGBColor.white();

        if (GsonHelper.isValidNode(json, "color")) {
            JsonObject colorEntry = GsonHelper.getAsJsonObject(json, "color");
            if (GsonHelper.isValidNode(colorEntry, "name")) {
                color = RGBColor.byName(GsonHelper.getAsString(colorEntry, "name"));
            } else if (GsonHelper.isValidNode(colorEntry, "red")) {
                color = RGBColor.fromJsonFloat(colorEntry);
            }
        }

        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "ingredients")) {
            JsonObject entry = e.getAsJsonObject();
            ingredients.add(Ingredient.fromJson(entry));
        }

        NonNullList<ProcessingOutput> results = NonNullList.create();
        for (JsonElement e : GsonHelper.getAsJsonArray(json, "results")) {
            JsonObject entry = e.getAsJsonObject();
            results.add(ProcessingOutput.deserialize(entry));
        }

        return factory.create(recipeId, ingredients, results, power, color);
    }

    @Nullable
    @Override
    public SimpleBeaconRecipe<?> fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        int power = buffer.readInt();
        RGBColor color = new RGBColor(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());

        NonNullList<Ingredient> ingredients = NonNullList.create();
        int ingredientCount = buffer.readInt();
        for (int i = 0; i < ingredientCount; i++)
            ingredients.add(Ingredient.fromNetwork(buffer));

        NonNullList<ProcessingOutput> results = NonNullList.create();
        int outputCount = buffer.readInt();
        for (int i = 0; i < outputCount; i++)
            results.add(ProcessingOutput.read(buffer));
        return factory.create(recipeId, ingredients, results, power, color);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SimpleBeaconRecipe<?> recipe) {
        buffer.writeInt(recipe.power);
        for (float c : recipe.color.asFloatArray())
            buffer.writeFloat(c);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        buffer.writeInt(ingredients.size());
        ingredients.forEach(i -> Ingredient.fromNetwork(buffer));

        buffer.writeInt(recipe.getRollableResults().size());
        recipe.getRollableResults().forEach(i -> i.write(buffer));
    }

    @FunctionalInterface
    public interface IRecipeFactory {

        SimpleBeaconRecipe<?> create(ResourceLocation recipeId, NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results, int power, RGBColor color);

    }

}
