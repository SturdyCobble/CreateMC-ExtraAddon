package sturdycobble.createrevision.api.heat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class HeatRecipeSerializer<T extends HeatRecipe<?>> extends ForgeRegistryEntry<IRecipeSerializer<?>>
        implements IRecipeSerializer<T> {

    protected final IRecipeFactory<T> factory;

    public HeatRecipeSerializer(IRecipeFactory<T> factoryIn) {
        factory = factoryIn;
    }

    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "ingredients")) {
            JsonObject entry = e.getAsJsonObject();
            if (JSONUtils.isValidNode(entry, "fluid")) {
                addFluidIngredientToList(fluidIngredients, entry);
            } else {
                long count = JSONUtils.getAsLong(entry, "count", 1);
                for (long i = 0; i < count; i++) {
                    ingredients.add(Ingredient.fromJson(entry));
                }
            }
        }

        NonNullList<ProcessingOutput> results = NonNullList.create();
        NonNullList<FluidStack> fluidResults = NonNullList.create();
        for (JsonElement e : JSONUtils.getAsJsonArray(json, "results")) {
            JsonObject entry = e.getAsJsonObject();
            if (JSONUtils.isValidNode(entry, "fluid")) {
                addFluidToList(fluidResults, entry);
            } else {
                String s1 = JSONUtils.getAsString(entry, "item");
                int i = JSONUtils.getAsInt(entry, "count", 1);
                float chance = 1;
                if (JSONUtils.isValidNode(entry, "chance"))
                    chance = JSONUtils.getAsFloat(entry, "chance");
                ItemStack itemstack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);
                results.add(new ProcessingOutput(itemstack, chance));
            }
        }

        float heatProduction = JSONUtils.getAsFloat(json, "heatProduction", 0);
        float tempMin = JSONUtils.getAsFloat(json, "tempMin", 0);
        float tempMax = JSONUtils.getAsFloat(json, "tempMax", 10000);
        int processingDuration = JSONUtils.getAsInt(json, "processingTime", 0);

        return this.factory.create(recipeId, ingredients, results, fluidIngredients, fluidResults, heatProduction,
                tempMin, tempMax, processingDuration);
    }

    private void addFluidToList(NonNullList<FluidStack> fluidStacks, JsonObject entry) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(JSONUtils.getAsString(entry, "fluid")));
        int amount = 1;
        if (JSONUtils.isValidNode(entry, "amount")) {
            amount = JSONUtils.getAsInt(entry, "amount");
        }
        if (fluid != null && amount > 0)
            fluidStacks.add(new FluidStack(fluid, amount));
    }

    private void addFluidIngredientToList(NonNullList<FluidIngredient> fluidStacks, JsonObject entry) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(JSONUtils.getAsString(entry, "fluid")));
        int amount = 1;
        if (JSONUtils.isValidNode(entry, "amount")) {
            amount = JSONUtils.getAsInt(entry, "amount");
        }
        if (fluid != null && amount > 0)
            fluidStacks.add(FluidIngredient.fromFluid(fluid, amount));
    }

    @Override
    public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        int ingredientCount = buffer.readInt();
        for (int i = 0; i < ingredientCount; i++)
            ingredients.add(Ingredient.fromNetwork(buffer));

        int fluidInputCount = buffer.readInt();
        NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
        for (int i = 0; i < fluidInputCount; i++)
            fluidIngredients.add(FluidIngredient.read(buffer));

        NonNullList<ProcessingOutput> results = NonNullList.create();
        int outputCount = buffer.readInt();
        for (int i = 0; i < outputCount; i++)
            results.add(ProcessingOutput.read(buffer));

        int fluidOutputCount = buffer.readInt();
        NonNullList<FluidStack> fluidResults = NonNullList.create();
        for (int i = 0; i < fluidOutputCount; i++)
            fluidResults.add(FluidStack.readFromPacket(buffer));

        float heatProduction = buffer.readFloat();
        float tempMin = buffer.readFloat();
        float tempMax = buffer.readFloat();
        int processingDuration = buffer.readInt();

        return this.factory.create(recipeId, ingredients, results, fluidIngredients, fluidResults, heatProduction,
                tempMin, tempMax, processingDuration);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, T recipe) {
        NonNullList<Ingredient> ingredList = recipe.getIngredients();
        buffer.writeInt(ingredList.size());
        ingredList.forEach(i -> Ingredient.fromNetwork(buffer));
        if (recipe.getFluidIngredients() != null) {
            buffer.writeInt(recipe.getFluidIngredients().size());
            recipe.getFluidIngredients().forEach(fluidStack -> fluidStack.write(buffer));
        } else {
            buffer.writeInt(0);
        }

        buffer.writeInt(recipe.getRollableResults().size());
        recipe.getRollableResults().forEach(i -> i.write(buffer));
        if (recipe.getFluidResults() != null) {
            buffer.writeInt(recipe.getFluidResults().size());
            recipe.getFluidResults().forEach(fluidStack -> fluidStack.writeToPacket(buffer));
        } else {
            buffer.writeInt(0);
        }

        buffer.writeFloat((float) recipe.getHeatProduction());
        buffer.writeFloat((float) recipe.getTempMin());
        buffer.writeFloat((float) recipe.getTempMax());
        buffer.writeInt(recipe.getDuration());

    }

    @FunctionalInterface
    public interface IRecipeFactory<T extends HeatRecipe<?>> {

        T create(ResourceLocation recipeId, NonNullList<Ingredient> ingredients, NonNullList<ProcessingOutput> results,
                 NonNullList<FluidIngredient> fluidIngredients, NonNullList<FluidStack> fluidResults,
                 float heatProduction, float tempMin, float tempMax, int processingDuration);

    }

}
