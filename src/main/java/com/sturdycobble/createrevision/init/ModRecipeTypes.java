package com.sturdycobble.createrevision.init;

import java.util.Optional;
import java.util.function.Supplier;

import com.simibubi.create.foundation.utility.Lang;
import com.sturdycobble.createrevision.CreateRevision;
import com.sturdycobble.createrevision.api.heat.HeatRecipeSerializer;
import com.sturdycobble.createrevision.contents.heat.FrictionRecipe;
import com.sturdycobble.createrevision.contents.heat.HeatExchangerRecipe;
import com.sturdycobble.createrevision.contents.heat.HeatExchangerRecipeSerializer;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

public enum ModRecipeTypes {

	HEAT_EXCHANGER(() -> new HeatExchangerRecipeSerializer(HeatExchangerRecipe::new)),
	FRICTION(() -> new HeatRecipeSerializer<FrictionRecipe>(FrictionRecipe::new));

	public IRecipeSerializer<?> serializer;
	public Supplier<IRecipeSerializer<?>> supplier;
	public IRecipeType<? extends IRecipe<? extends IInventory>> type;

	ModRecipeTypes(Supplier<IRecipeSerializer<?>> supplier) {
		this(supplier, null);
	}

	ModRecipeTypes(Supplier<IRecipeSerializer<?>> supplier,
			IRecipeType<? extends IRecipe<? extends IInventory>> existingType) {
		this.supplier = supplier;
		this.type = existingType;
	}

	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		for (ModRecipeTypes r : ModRecipeTypes.values()) {
			if (r.type == null)
				r.type = customType(Lang.asId(r.name()));

			r.serializer = r.supplier.get();
			ResourceLocation location = new ResourceLocation(CreateRevision.MODID, Lang.asId(r.name()));
			event.getRegistry().register(r.serializer.setRegistryName(location));
		}
	}

	private static <T extends IRecipe<?>> IRecipeType<T> customType(String id) {
		return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateRevision.MODID, id),
				new IRecipeType<T>() {
					public String toString() {
						return CreateRevision.MODID + ":" + id;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public <T extends IRecipeType<? extends IRecipe<?>>> T getType() {
		return (T) type;
	}

	public <C extends IInventory, T extends IRecipe<C>> Optional<T> find(C inv, World world) {
		return world.getRecipeManager().getRecipe(getType(), inv, world);
	}

}
