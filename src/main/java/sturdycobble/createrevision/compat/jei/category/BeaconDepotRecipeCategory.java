package sturdycobble.createrevision.compat.jei.category;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.contents.reinforced_depot.BeaconDepotRecipe;
import sturdycobble.createrevision.init.ModBlocks;
import sturdycobble.createrevision.init.ModItems;
import sturdycobble.createrevision.utils.ColorCondition;
import sturdycobble.createrevision.utils.ColorConditions;
import sturdycobble.createrevision.utils.ColorGlassCombination;
import sturdycobble.createrevision.utils.RGBColor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BeaconDepotRecipeCategory extends CreateRecipeCategory<BeaconDepotRecipe> {

    protected static final String NAME = "beacon_depot_recipe";

    private ColorCondition color;
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
        AllGuiTextures.JEI_LONG_ARROW.render(poseStack, 37, 32);

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

        color = recipe.getColorCondition();
        power = recipe.getPower();
    }

    @Override
    public void draw(@Nullable BeaconDepotRecipe recipe, @Nullable PoseStack poseStack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        Component[] components = color.getDescription();

        if (poseStack != null) {
            renderWidgets(poseStack, recipe);
            poseStack.pushPose();
            if (components.length > 0) {
                Component component1 = components[0];
                font.draw(poseStack, component1, (177 - font.width(component1)) / 2, 1, 0x888888);
            }
            if (components.length > 1) {
                Component component2 = components[1];
                font.draw(poseStack, component2, (177 - font.width(component2)) / 2, 9, 0x888888);
            }
            Component component3 = new TranslatableComponent("createrevision.jei.beacon_depot.level.desc", power).withStyle(ChatFormatting.BOLD);
            font.draw(poseStack, component3, (177 - font.width(component3)) / 2, 60, 0x888888);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-12.5F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(22.5F));
            GuiGameElement.of(ModBlocks.REINFORCED_DEPOT.get().defaultBlockState())
                    .scale(12)
                    .atLocal(5.7, 2.4, 2)
                    .render(poseStack);
            GuiGameElement.of(Blocks.BEACON.defaultBlockState())
                    .scale(12)
                    .atLocal(5.7, 4.4, 2)
                    .render(poseStack);
            poseStack.popPose();
        }
    }

    @Override
    public List<Component> getTooltipStrings(BeaconDepotRecipe recipe, double mouseX, double mouseY) {
        int cnt = 0;

        if (mouseX < 150 && mouseX > 27 && mouseY > 2 && mouseY < 16) {
            if (recipe.getColorCondition().getID().equals(ColorConditions.RANDOM.getID())) {
                TranslatableComponent randomComponent = new TranslatableComponent(CreateRevision.MODID + ".jei.beacon_depot.tooltip.random");
                return ImmutableList.of(randomComponent);
            } else if (recipe.getColorCondition().getID().equals(ColorConditions.NONE.getID())) {
                return Collections.emptyList();
            }

            TranslatableComponent headComponent = new TranslatableComponent(CreateRevision.MODID + ".jei.beacon_depot.tooltip.combination");
            MutableComponent mutableComponent = new TextComponent("");
            for (int[] triple : ColorGlassCombination.MIXED_COLORS) {
                if (recipe.getColorCondition().test(new RGBColor(triple[0]))) {
                    if (cnt % 5 == 0)
                        mutableComponent.append(new TextComponent("\n| "));
                    cnt++;
                    mutableComponent.append(new TextComponent("\u2588").withStyle(
                            Style.EMPTY.applyFormat(ChatFormatting.BLACK).withColor(DyeColor.byId(triple[1]).getTextColor())));
                    mutableComponent.append(new TextComponent("\u2588").withStyle(
                            Style.EMPTY.applyFormat(ChatFormatting.BLACK).withColor(DyeColor.byId(triple[2]).getTextColor())));
                    mutableComponent.append(new TextComponent(" | "));
                    if (cnt >= 33) {
                        mutableComponent.append(new TextComponent(" \u2022\u2022\u2022"));
                        break;
                    }
                }
            }
            if (cnt > 0)
                return ImmutableList.of(headComponent, mutableComponent);
        }
        return Collections.emptyList();
    }

}
