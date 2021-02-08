package com.sturdycobble.createrevision.contents.heat;

import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.sturdycobble.createrevision.api.heat.HeatRecipe;
import com.sturdycobble.createrevision.init.ModRecipeTypes;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerRecipe<C extends IInventory> implements HeatRecipe<C> {

	protected ResourceLocation id;
	protected Block blockInput;
	protected Block blockOutput;
	protected int processingDuration;
	protected final float heatProduction;
	protected final float tempMin;
	protected final float tempMax;
	private final IRecipeType<?> type;
	private final IRecipeSerializer<?> serializer;

	public HeatExchangerRecipe(ResourceLocation idIn, Block blockInputIn, Block blockOutputIn, float heatIn,
			float tempMinIn, float tempMaxIn, int durationIn) {
		type = ModRecipeTypes.HEAT_EXCHANGER.getType();
		serializer = ModRecipeTypes.HEAT_EXCHANGER.serializer;
		id = idIn;
		blockInput = blockInputIn;
		blockOutput = blockOutputIn;
		processingDuration = durationIn;
		heatProduction = heatIn;
		tempMin = tempMinIn;
		tempMax = tempMaxIn;
	}

	public Block getInputBlock() {
		return this.blockInput;
	}

	public Block getOutputBlock() {
		return this.blockOutput;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return false;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return null;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(Items.AIR);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return serializer;
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}

	@Override
	public double getHeatProduction() {
		return heatProduction;
	}

	@Override
	public double getTempMin() {
		return tempMin;
	}

	@Override
	public double getTempMax() {
		return tempMax;
	}

	@Override
	public int getDuration() {
		return processingDuration;
	}

	@Override
	public NonNullList<FluidIngredient> getFluidIngredients() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<ProcessingOutput> getRollableResults() {
		return NonNullList.create();
	}

	@Override
	public NonNullList<FluidStack> getFluidResults() {
		return NonNullList.create();
	}

}