package sturdycobble.createrevision.contents.custom_fan;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sturdycobble.createrevision.utils.FluidOrBlock;

import javax.annotation.Nullable;

public class CustomFanRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>>
        implements RecipeSerializer<CustomFanRecipe> {

    protected final IRecipeFactory factory;

    public CustomFanRecipeSerializer(IRecipeFactory factoryIn) {
        factory = factoryIn;
    }

    @Override
    public CustomFanRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject sourceEntry = GsonHelper.getAsJsonObject(json, "source");
        FluidOrBlock sourceFluidOrBlock = FluidOrBlock.empty();

        if (GsonHelper.isValidNode(sourceEntry, "fluid")) {
            sourceFluidOrBlock = new FluidOrBlock(getFluidFromJson(sourceEntry));
        } else if (GsonHelper.isValidNode(sourceEntry, "block")) {
            sourceFluidOrBlock = new FluidOrBlock(getBlockFromJson(sourceEntry));
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
        return factory.create(recipeId, ingredients, results, sourceFluidOrBlock);
    }

    private Block getBlockFromJson(JsonObject entry) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(entry, "block")));
    }

    private Fluid getFluidFromJson(JsonObject entry) {
        return ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(GsonHelper.getAsString(entry, "fluid")));
    }

    @Nullable
    @Override
    public CustomFanRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        FluidOrBlock source = FluidOrBlock.fromString(buffer.readUtf());

        NonNullList<Ingredient> ingredients = NonNullList.create();
        int ingredientCount = buffer.readInt();
        for (int i = 0; i < ingredientCount; i++)
            ingredients.add(Ingredient.fromNetwork(buffer));

        NonNullList<ProcessingOutput> results = NonNullList.create();
        int outputCount = buffer.readInt();
        for (int i = 0; i < outputCount; i++)
            results.add(ProcessingOutput.read(buffer));
        return factory.create(recipeId, ingredients, results, source);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CustomFanRecipe recipe) {
        buffer.writeUtf(recipe.sourceType.toString());

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        buffer.writeInt(ingredients.size());
        ingredients.forEach(i -> Ingredient.fromNetwork(buffer));

        buffer.writeInt(recipe.getRollableResults().size());
        recipe.getRollableResults().forEach(i -> i.write(buffer));
    }

    @FunctionalInterface
    public interface IRecipeFactory {

        CustomFanRecipe create(ResourceLocation recipeId, NonNullList<Ingredient> ingredients,
                                  NonNullList<ProcessingOutput> results, FluidOrBlock source);

    }

}