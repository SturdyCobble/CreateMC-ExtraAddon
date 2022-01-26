package sturdycobble.createrevision.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.reinforced_depot.BeaconDepotRecipe;
import sturdycobble.createrevision.init.ModBlocks;
import sturdycobble.createrevision.init.ModItems;
import sturdycobble.createrevision.utils.RGBColor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BeaconDepotRecipeCategory extends CreateRecipeCategory<BeaconDepotRecipe> {

    protected static final String NAME = "beacon_depot_recipe";

    private RGBColor color;
    private int power;

    public BeaconDepotRecipeCategory() {
        super(doubleItemIcon(Items.BEACON, ModItems.REINFORCED_DEPOT.get()), emptyBackground(177, 71));
        uid = getId();
    }

    public static ResourceLocation getId() {
        return new ResourceLocation(CreateRevision.MODID, NAME);
    }

    protected void renderWidgets(PoseStack poseStack, BeaconDepotRecipe recipe) {
        int size = recipe.getRollableResultsAsItemStacks().size();

        AllGuiTextures.JEI_SLOT.render(poseStack, 6, 27);
        AllGuiTextures.JEI_LONG_ARROW.render(poseStack, 37, 30);

        if (size == 1) {
            getRenderedSlot(recipe, 0).render(poseStack, 118, 27);
            return;
        }

        for (int i = 0; i < size; i++) {
            int xOffset = (i % 3) * 19;
            int yOffset = (size <= 3) ? 0 : -19 + (i / 3) * 19;
            getRenderedSlot(recipe, i).render(poseStack, 118 + xOffset, 27 + yOffset);
        }
    }

    @Override
    public Class<? extends BeaconDepotRecipe> getRecipeClass() {
        return BeaconDepotRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("createrevision.recipe." + NAME);
    }

    @Override
    public void setIngredients(BeaconDepotRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, recipe.getRollableResultsAsItemStacks());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BeaconDepotRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        List<ProcessingOutput> results = recipe.getRollableResults();

        itemStacks.init(0, true, 6, 27);
        itemStacks.set(0, Arrays.asList(recipe.getIngredients().get(0).getItems()));

        boolean single = results.size() == 1;
        for (int outputIndex = 0; outputIndex < results.size(); outputIndex++) {
            int xOffset = (outputIndex % 3) * 19;
            int yOffset = (results.size() <= 3) ? 0 : -19 + (outputIndex / 3) * 19;

            itemStacks.init(outputIndex + 1, false, (single ? 118 : 118 + xOffset), 27 + yOffset);
            itemStacks.set(outputIndex + 1, results.get(outputIndex).getStack());
        }

        addStochasticTooltip(itemStacks, results);

        color = recipe.getColor();
        power = recipe.getPower();
    }

    @Override
    public void draw(@Nullable BeaconDepotRecipe recipe, @Nullable PoseStack poseStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        String colorInHex = color.toHex();

        if (poseStack != null) {
            renderWidgets(poseStack, recipe);
            poseStack.pushPose();
            Component component1 = new TextComponent("Color: #" + colorInHex).withStyle(ChatFormatting.BOLD);
            font.drawShadow(poseStack, component1, (177 - font.width(component1)) / 2, 5, color.toInt());
            Component component2 = new TextComponent("Minimum Level: " + power).withStyle(ChatFormatting.BOLD);
            font.drawShadow(poseStack, component2, (177 - font.width(component2)) / 2, 60, 0x888888);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-12.5F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(22.5F));
            GuiGameElement.of(ModBlocks.REINFORCED_DEPOT.get().defaultBlockState())
                    .scale(12)
                    .atLocal(6, 2.3, 2)
                    .render(poseStack);
            GuiGameElement.of(Blocks.BEACON.defaultBlockState())
                    .scale(12)
                    .atLocal(6, 4.3, 2)
                    .render(poseStack);
            poseStack.popPose();
        }
    }

}
