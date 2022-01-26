package sturdycobble.createrevision.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.custom_fan.CustomFanRecipe;
import sturdycobble.createrevision.utils.FluidOrBlock;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CustomFanRecipeCategory extends ProcessingViaFanCategory<CustomFanRecipe> {

    protected static final String NAME = "custom_fan_recipe";

    private FluidOrBlock sourceType;

    public CustomFanRecipeCategory() {
        super(doubleItemIcon(AllItems.PROPELLER.get(), Items.FERN));
        uid = getId();
    }

    public static ResourceLocation getId() {
        return new ResourceLocation(CreateRevision.MODID, NAME);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("createrevision.recipe." + NAME);
    }

    @Override
    public void setIngredients(CustomFanRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getRollableResultsAsItemStacks());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CustomFanRecipe recipe, @Nullable IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        List<ProcessingOutput> results = recipe.getRollableResults();
        int xOffsetGlobal = 8 * (3 - Math.min(3, results.size()));

        itemStacks.init(0, true, xOffsetGlobal + 12, 47);
        itemStacks.set(0, Arrays.asList(recipe.getIngredients().get(0).getItems()));

        boolean single = results.size() == 1;
        boolean excessive = results.size() > 9;
        for (int outputIndex = 0; outputIndex < results.size(); outputIndex++) {
            int xOffset = (outputIndex % 3) * 19;
            int yOffset = (outputIndex / 3) * -19;

            itemStacks.init(outputIndex + 1, false, xOffsetGlobal + (single ? 126 : 126 + xOffset), 47 + yOffset + (excessive ? 8 : 0));
            itemStacks.set(outputIndex + 1, results.get(outputIndex).getStack());
        }

        addStochasticTooltip(itemStacks, results);

        sourceType = recipe.getSourceType();
    }

    @Override
    public void renderAttachedBlock(PoseStack poseStack) {
        GuiGameElement.of(sourceType.getBlockState())
                .scale(24)
                .atLocal(0, 0, 2)
                .render(poseStack);
    }

    @Override
    public Class<? extends CustomFanRecipe> getRecipeClass() {
        return CustomFanRecipe.class;
    }

    @Override
    protected void renderWidgets(PoseStack poseStack, CustomFanRecipe recipe, double mouseX, double mouseY) {
        int size = recipe.getRollableResultsAsItemStacks().size();
        int xOffsetGlobal = 8 * (3 - Math.min(3, size));

        AllGuiTextures.JEI_SLOT.render(poseStack, xOffsetGlobal + 12, 47);
        AllGuiTextures.JEI_SHADOW.render(poseStack, 47 + 4, 29);
        AllGuiTextures.JEI_SHADOW.render(poseStack, 66 + 4, 39);
        AllGuiTextures.JEI_LONG_ARROW.render(poseStack, xOffsetGlobal + 42, 51);

        if (size == 1) {
            getRenderedSlot(recipe, 0).render(poseStack, xOffsetGlobal + 126, 47);
            return;
        }

        for (int i = 0; i < size; i++) {
            int xOffset = (i % 3) * 19;
            int yOffset = (i / 3) * -19 + (size > 9 ? 8 : 0);
            getRenderedSlot(recipe, i).render(poseStack, xOffsetGlobal + 126 + xOffset, 47 + yOffset);
        }
    }

}
