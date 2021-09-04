package sturdycobble.createrevision.contents.heat;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class HeatExchangerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
        implements IRecipeSerializer<HeatExchangerRecipe<?>> {

    protected final IRecipeFactory factory;

    public HeatExchangerRecipeSerializer(IRecipeFactory factoryIn) {
        factory = factoryIn;
    }

    @Override
    public HeatExchangerRecipe<?> fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject input = JSONUtils.getAsJsonObject(json, "input");
        JsonObject output = JSONUtils.getAsJsonObject(json, "output");
        Block blockIn = getBlockFromJsonObject(input);
        Block blockOut = getBlockFromJsonObject(output);
        float heatProduction = JSONUtils.getAsFloat(json, "heatProduction", 0);
        float tempMin = JSONUtils.getAsFloat(json, "tempMin", 0);
        float tempMax = JSONUtils.getAsFloat(json, "tempMax", 10000);
        int processingDuration = JSONUtils.getAsInt(json, "processingTime", 0);

        return this.factory.create(recipeId, blockIn, blockOut, heatProduction, tempMin, tempMax, processingDuration);
    }

    private Block getBlockFromJsonObject(JsonObject entry) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(JSONUtils.getAsString(entry, "block")));
    }

    @Override
    public HeatExchangerRecipe<?> fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        Block blockIn = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(buffer.toString()));
        Block blockOut = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(buffer.toString()));
        float heatProduction = buffer.readFloat();
        float tempMin = buffer.readFloat();
        float tempMax = buffer.readFloat();
        int processingDuration = buffer.readInt();
        return this.factory.create(recipeId, blockIn, blockOut, heatProduction, tempMin, tempMax, processingDuration);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, HeatExchangerRecipe<?> recipe) {
        buffer = buffer.writeUtf(recipe.blockInput.getDescriptionId());
        buffer = buffer.writeUtf(recipe.blockOutput.getDescriptionId());
        buffer.writeFloat(recipe.heatProduction);
        buffer.writeFloat(recipe.tempMin);
        buffer.writeFloat(recipe.tempMax);
        buffer.writeInt(recipe.processingDuration);
    }

    @FunctionalInterface
    public interface IRecipeFactory {

        HeatExchangerRecipe<?> create(ResourceLocation recipeId, Block blockIn, Block blockOut, float heatProduction,
                                      float tempMin, float tempMax, int processingDuration);

    }

}
