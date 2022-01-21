package sturdycobble.createrevision.init;

import com.simibubi.create.foundation.utility.Lang;
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
import sturdycobble.createrevision.contents.CustomFanRecipe;
import sturdycobble.createrevision.contents.CustomFanRecipeSerializer;
import sturdycobble.createrevision.mixin.MixinRecipeManager;
import sturdycobble.createrevision.utils.FluidOrBlock;

import java.util.Optional;
import java.util.function.Supplier;

public enum ModRecipeTypes {

    CUSTOM_FAN_RECIPE(() -> new CustomFanRecipeSerializer(CustomFanRecipe::new));

    public RecipeSerializer<?> serializer;
    public Supplier<RecipeSerializer<?>> supplier;
    public RecipeType<? extends Recipe<? extends Container>> type;

    ModRecipeTypes(Supplier<RecipeSerializer<?>> supplier) {
        this(supplier, null);
    }

    ModRecipeTypes(Supplier<RecipeSerializer<?>> supplier,
                   RecipeType<? extends Recipe<? extends Container>> existingType) {
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
        return ((MixinRecipeManager) world.getRecipeManager()).invokeToByType(recipeType).values().stream().flatMap(r -> {
            return Util.toStream(((CustomFanRecipe<C>) r).matches(inv, world, type) ?
                    Optional.of((CustomFanRecipe<C>) r) : Optional.empty());
        }).findFirst();
    }

    @SuppressWarnings("unchecked")
    public <T extends RecipeType<? extends Recipe<?>>> T getType() {
        return (T) type;
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> find(C inv, Level world) {
        return world.getRecipeManager().getRecipeFor(getType(), inv, world);
    }

}
