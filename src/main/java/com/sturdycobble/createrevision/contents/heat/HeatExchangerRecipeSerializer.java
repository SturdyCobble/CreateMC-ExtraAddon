package com.sturdycobble.createrevision.contents.heat;

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
	public HeatExchangerRecipe<?> read(ResourceLocation recipeId, JsonObject json) {
		JsonObject input = JSONUtils.getJsonObject(json, "input");
		JsonObject output = JSONUtils.getJsonObject(json, "output");
		Block blockIn = getBlockFromJsonObject(input);
		Block blockOut = getBlockFromJsonObject(output);
		float heatProduction = JSONUtils.getFloat(json, "heatProduction", 0);
		float tempMin = JSONUtils.getFloat(json, "tempMin", 0);
		float tempMax = JSONUtils.getFloat(json, "tempMax", 10000);
		int processingDuration = JSONUtils.getInt(json, "processingTime", 0);

		return this.factory.create(recipeId, blockIn, blockOut, heatProduction, tempMin, tempMax, processingDuration);
	}

	private Block getBlockFromJsonObject(JsonObject entry) {
		return ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(JSONUtils.getString(entry, "block")));
	}

	@Override
	public HeatExchangerRecipe<?> read(ResourceLocation recipeId, PacketBuffer buffer) {
		Block blockIn = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(buffer.readString()));
		Block blockOut = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(buffer.readString()));
		float heatProduction = buffer.readFloat();
		float tempMin = buffer.readFloat();
		float tempMax = buffer.readFloat();
		int processingDuration = buffer.readInt();
		return this.factory.create(recipeId, blockIn, blockOut, heatProduction, tempMin, tempMax, processingDuration);
	}

	@Override
	public void write(PacketBuffer buffer, HeatExchangerRecipe<?> recipe) {
		buffer = buffer.writeString(recipe.blockInput.getTranslationKey());
		buffer = buffer.writeString(recipe.blockOutput.getTranslationKey());
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
