package sturdycobble.createrevision.init;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.api.depot_recipe.BeaconRecipe;
import sturdycobble.createrevision.api.depot_recipe.BeaconRecipeSerializer;
import sturdycobble.createrevision.api.depot_recipe.SimpleBeaconRecipe;
import sturdycobble.createrevision.contents.custom_fan.CustomFanRecipe;
import sturdycobble.createrevision.contents.custom_fan.CustomFanRecipeSerializer;
import sturdycobble.createrevision.contents.reinforced_depot.BeaconDepotRecipe;
import sturdycobble.createrevision.mixin.RecipeManagerInvoker;
import sturdycobble.createrevision.utils.FluidOrBlock;
import sturdycobble.createrevision.utils.RGBColor;

import java.util.Optional;
import java.util.function.Supplier;

public enum ModRecipeTypes implements IRecipeTypeInfo {

    CUSTOM_FAN_RECIPE(() -> new CustomFanRecipeSerializer(CustomFanRecipe::new)),
    BEACON_DEPOT_RECIPE(() -> new BeaconRecipeSerializer(BeaconDepotRecipe::new));

    public RecipeSerializer<? extends Recipe> serializer;
    public Supplier<RecipeSerializer> supplier;
    public RecipeType<? extends Recipe<? extends Container>> type;

    private final ResourceLocation id;

    ModRecipeTypes(Supplier<RecipeSerializer> supplier) {
        this(supplier, null);
    }

    ModRecipeTypes(Supplier<RecipeSerializer> supplier,
                   RecipeType<? extends Recipe<? extends Container>> existingType) {
        this.id = new ResourceLocation(CreateRevision.MODID, Lang.asId(name()));
        this.supplier = supplier;
        this.type = existingType;
    }

    public static void register(RegistryEvent.Register<RecipeSerializer<?>> event) {
        for (ModRecipeTypes r : ModRecipeTypes.values()) {
            if (r.type == null)
                r.type = customType(Lang.asId(r.name()));

            r.serializer = r.supplier.get();
            ResourceLocation location = new ResourceLocation(CreateRevision.MODID, Lang.asId(r.name()));
            event.getRegistry().register(r.serializer.setRegistryName(location));
        }
    }

    private static <T extends Recipe<?>> RecipeType<T> customType(String id) {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CreateRevision.MODID, id),
                new RecipeType<T>() {
                    public String toString() {
                        return CreateRevision.MODID + ":" + id;
                    }
                });
    }

    public static <C extends Container> Optional<CustomFanRecipe> findCustomFanRecipe(C inv, Level world, FluidOrBlock type) {
        RecipeType recipeType = CUSTOM_FAN_RECIPE.getType();
        return ((RecipeManagerInvoker) world.getRecipeManager()).invokeToByType(recipeType).values().stream().flatMap(r -> {
            return Util.toStream(((CustomFanRecipe) r).matches(inv, world, type) ?
                    Optional.of((CustomFanRecipe) r) : Optional.empty());
        }).findFirst();
    }

    public static <C extends Container> Optional<SimpleBeaconRecipe> findBeaconRecipe(ModRecipeTypes type, C inv, Level world, int power, RGBColor color) {
        RecipeType recipeType = type.getType();
        return ((RecipeManagerInvoker) world.getRecipeManager()).invokeToByType(recipeType).values().stream().flatMap(r -> {
            return Util.toStream(((BeaconRecipe<C>) r).matches(inv, world, power, color) ?
                    Optional.of((BeaconRecipe<C>) r) : Optional.empty());
        }).findFirst();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) type;
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), inv, world);
    }

}
