package sturdycobble.createrevision.api.depot_recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import sturdycobble.createrevision.utils.ColorCondition;
import sturdycobble.createrevision.utils.ColorConditions;
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

        ColorCondition color = ColorConditions.NONE.create(null);

        if (GsonHelper.isValidNode(json, "color")) {
            String colorString = GsonHelper.getAsString(json, "color");
            color = RGBColor.getColorMatchConditionFromString(colorString);
        } else if (GsonHelper.isValidNode(json, "mixed color")) {
            JsonArray mixedArr = GsonHelper.getAsJsonArray(json, "mixed color");
            RGBColor result = RGBColor.byName(mixedArr.get(0).getAsString());
            for (JsonElement e : mixedArr) {
                String c = e.getAsString();
                result = result.mixWith(RGBColor.byName(c));
            }
            color = ColorConditions.COLOR.create(result);
        } else if (GsonHelper.isValidNode(json, "color filter")) {
            JsonObject colorEntry = json.getAsJsonObject("color filter");
            if (GsonHelper.isValidNode(colorEntry, "type")) {
                String condString = GsonHelper.getAsString(colorEntry, "type");
                ColorConditions condition = ColorConditions.fromName(condString);
                RGBColor.RequiredType type = condition.requiredType();

                if (type == RGBColor.RequiredType.COLOR && GsonHelper.isValidNode(colorEntry, "color")){
                    String centerString = GsonHelper.getAsString(json, "color");
                    RGBColor center = RGBColor.byString(centerString);
                    color = condition.create(center);
                } else if (type == RGBColor.RequiredType.SEED && GsonHelper.isValidNode(colorEntry, "seed")) {
                    long seed = GsonHelper.getAsLong(colorEntry, "seed");
                    color = condition.create(seed);
                } else if (type == RGBColor.RequiredType.CUBE_RANGE && GsonHelper.isValidNode(colorEntry, "ranges")) {
                    JsonArray array = GsonHelper.getAsJsonArray(colorEntry, "ranges");
                    float[] ranges = new float[6];
                    for (int i = 0; i < 6; i++) {
                        float fv = array.get(i).getAsFloat();
                        ranges[i] = fv;
                    }
                    color = condition.create(ranges);
                } else if (type == RGBColor.RequiredType.RANGE && GsonHelper.isValidNode(colorEntry, "range")) {
                    JsonArray array = GsonHelper.getAsJsonArray(colorEntry, "range");
                    float[] range = new float[2];
                    for (int i = 0; i < 2; i++) {
                        float fv = array.get(i).getAsFloat();
                        range[i] = fv;
                    }
                    color = condition.create(range);
                } else if (type == RGBColor.RequiredType.RADIUS && GsonHelper.isValidNode(colorEntry, "color") && GsonHelper.isValidNode(colorEntry, "radius") ) {
                    float radius = GsonHelper.getAsFloat(colorEntry, "radius");
                    RGBColor center = RGBColor.byString(GsonHelper.getAsString(colorEntry, "color"));
                    color = condition.create(Pair.of(center, radius));
                } else {
                    color = condition.create(null);
                }
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
        int colorOrdinal = buffer.readInt();
        float[] arr = new float[ColorCondition.SERIAL_SIZE];
        for (int i = 0; i < ColorCondition.SERIAL_SIZE; i++)
            arr[i] = buffer.readFloat();
        ColorCondition color = ColorCondition.deserialize(Pair.of(colorOrdinal, arr));

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
        Pair<Integer, float[]> pair = recipe.colorCondition.serialize();
        buffer.writeInt(pair.getFirst());
        for (int i = 0; i < ColorCondition.SERIAL_SIZE; i++)
            buffer.writeFloat(pair.getSecond()[i]);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        buffer.writeInt(ingredients.size());
        ingredients.forEach(i -> Ingredient.fromNetwork(buffer));

        buffer.writeInt(recipe.getRollableResults().size());
        recipe.getRollableResults().forEach(i -> i.write(buffer));
    }

    @FunctionalInterface
    public interface IRecipeFactory {

        SimpleBeaconRecipe<?> create(ResourceLocation recipeId, NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results, int power, ColorCondition color);

    }

}
