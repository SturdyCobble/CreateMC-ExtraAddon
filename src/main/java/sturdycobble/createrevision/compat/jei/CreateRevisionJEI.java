package sturdycobble.createrevision.compat.jei;

import com.simibubi.create.AllBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.compat.jei.category.BeaconDepotRecipeCategory;
import sturdycobble.createrevision.compat.jei.category.CustomFanRecipeCategory;
import sturdycobble.createrevision.init.ModBlocks;
import sturdycobble.createrevision.init.ModRecipeTypes;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@JeiPlugin
@SuppressWarnings("unused")
public class CreateRevisionJEI implements IModPlugin {

    private static final ResourceLocation ID = new ResourceLocation(CreateRevision.MODID, "jei_plugin");

    @SuppressWarnings("unused")
    public CreateRevisionJEI() { }

    public static List<Recipe<?>> findRecipes(Predicate<Recipe<?>> predicate) {
        return Minecraft.getInstance()
                .getConnection()
                .getRecipeManager()
                .getRecipes()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CustomFanRecipeCategory(), new BeaconDepotRecipeCategory());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.BEACON), BeaconDepotRecipeCategory.getId());
        registration.addRecipeCatalyst(new ItemStack(AllBlocks.ENCASED_FAN.get()), CustomFanRecipeCategory.getId());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null)
            return;
        registration.addRecipes(findRecipes(r -> (r.getType() == ModRecipeTypes.CUSTOM_FAN_RECIPE.getType())), CustomFanRecipeCategory.getId());
        registration.addRecipes(findRecipes(r -> (r.getType() == ModRecipeTypes.BEACON_DEPOT_RECIPE.getType())), BeaconDepotRecipeCategory.getId());
    }

}
