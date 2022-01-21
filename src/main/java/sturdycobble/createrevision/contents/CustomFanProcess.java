package sturdycobble.createrevision.contents;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import sturdycobble.createrevision.init.ModRecipeTypes;
import sturdycobble.createrevision.utils.FluidOrBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomFanProcess {

    private static final CustomFanProcess.CustomFanRecipeWrapper CUSTOM_FAN_RECIPE_WRAPPER = new CustomFanProcess.CustomFanRecipeWrapper();

    public static boolean canCustomFanProcess(ItemEntity entity, FluidOrBlock type) {
        if (type.isEmpty() || type == null) return false;
        if (entity.getPersistentData().contains("CreateRevisionData")) {
            CompoundTag compound = entity.getPersistentData().getCompound("CreateRevisionData");
            if (compound.contains("Processing")) {
                CompoundTag processing = compound.getCompound("Processing");
                if (processing.getString("Type").equals(type.toString())) {
                    boolean canProcess = isCustomFanProcessible(entity.getItem(), entity.level, type);
                    processing.putString("Type", type.toString());
                    if (!canProcess) {
                        processing.putInt("Time", -1);
                    }
                    return canProcess;
                }

                if (processing.getInt("Time") >= 0) {
                    return true;
                }

                if (processing.getInt("Time") == -1) {
                    return false;
                }
            }
        }
        return isCustomFanProcessible(entity.getItem(), entity.getLevel(), type);
    }

    public static boolean isCustomFanProcessible(ItemStack stack, Level world, FluidOrBlock type) {
        CUSTOM_FAN_RECIPE_WRAPPER.setItem(0, stack);
        Optional<CustomFanRecipe> recipe = ModRecipeTypes.findCustomFanRecipe(CUSTOM_FAN_RECIPE_WRAPPER, world, type);
        return recipe.isPresent();
    }

    @Nullable
    private static List<ItemStack> customProcess(ItemStack stack, Level world, FluidOrBlock type) {
        CUSTOM_FAN_RECIPE_WRAPPER.setItem(0, stack);
        Optional<CustomFanRecipe> recipe = ModRecipeTypes.findCustomFanRecipe(CUSTOM_FAN_RECIPE_WRAPPER, world, type);
        return recipe.isPresent() ? applyCustomRecipeOn(stack, recipe.get()) : null;
    }

    private static int decrementCustomProcessingTime(ItemEntity entity, FluidOrBlock type) {
        CompoundTag nbt = entity.getPersistentData();
        if (!nbt.contains("CreateRevisionData")) {
            nbt.put("CreateRevisionData", new CompoundTag());
        }

        CompoundTag createData = nbt.getCompound("CreateRevisionData");
        if (!createData.contains("Processing")) {
            createData.put("Processing", new CompoundTag());
        }

        CompoundTag processing = createData.getCompound("Processing");
        int value;
        if (!processing.contains("Type") || !processing.getString("Type").equals(type.toString())) {
            processing.putString("Type", type.toString());
            value = (entity.getItem().getCount() - 1) / 16 + 1;
            int processingTime = AllConfigs.SERVER.kinetics.inWorldProcessingTime.get() * value + 1;
            processing.putInt("Time", processingTime);
        }

        value = processing.getInt("Time") - 1;
        processing.putInt("Time", value);
        return value;
    }

    public static void applyCustomProcessing(ItemEntity entity, FluidOrBlock type) {
        if (decrementCustomProcessingTime(entity, type) != 0) {
            return;
        }

        List<ItemStack> stacks = customProcess(entity.getItem(), entity.level, type);
        if (stacks == null)
            return;
        if (stacks.isEmpty()) {
            entity.discard();
            return;
        }

        entity.setItem(stacks.remove(0));
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level.addFreshEntity(entityIn);
        }
    }


    public static TransportedResult applyCustomProcessing(TransportedItemStack transported, Level world, FluidOrBlock type) {
        TransportedResult ignore = TransportedResult.doNothing();
        MixinTransportedItemStackAccess customTransported = (MixinTransportedItemStackAccess) transported;
        if (!customTransported.getCustomProcessType().equals(type)) {
            customTransported.setCustomProcessType(type);
            int timeModifierForStackSize = (((TransportedItemStack) customTransported).stack.getCount() - 1) / 16 + 1;
            int processingTime = AllConfigs.SERVER.kinetics.inWorldProcessingTime.get() * timeModifierForStackSize + 1;
            customTransported.setCustomProcessTime(processingTime);
            if (!isCustomFanProcessible(transported.stack, world, type)) {
                customTransported.setCustomProcessTime(-1);
            }

            return ignore;
        } else if (customTransported.getCustomProcessTime() == -1) {
            return ignore;
        } else if (customTransported.getCustomProcessTime() > 0) {
            customTransported.setCustomProcessTime(customTransported.getCustomProcessTime() - 1);
            return ignore;
        } else {
            List<ItemStack> stacks = customProcess(((TransportedItemStack) customTransported).stack, world, type);
            if (stacks == null) {
                return ignore;
            } else {
                List<TransportedItemStack> transportedStacks = new ArrayList();
                for (ItemStack additional : stacks) {
                    TransportedItemStack newTransported = ((TransportedItemStack) customTransported).getSimilar();
                    newTransported.stack = additional.copy();
                    transportedStacks.add(newTransported);
                }

                return TransportedResult.convertTo(transportedStacks);
            }
        }
    }

    public static List<ItemStack> applyCustomRecipeOn(ItemStack stackIn, Recipe<?> recipe) {
        List<ItemStack> stacks;
        if (recipe instanceof CustomFanRecipe) {
            stacks = new ArrayList<>();

            for (int i = 0; i < stackIn.getCount(); ++i) {
                List<ItemStack> rollResults = ((CustomFanRecipe) recipe).rollResults();

                for (ItemStack stack : rollResults) {
                    for (ItemStack previouslyRolled : stacks) {
                        if (stack.isEmpty())
                            continue;
                        if (ItemHandlerHelper.canItemStacksStack(stack, previouslyRolled))
                            continue;

                        int amount = Math.min(previouslyRolled.getMaxStackSize() - previouslyRolled.getCount(), stack.getCount());
                        previouslyRolled.grow(amount);
                        stack.shrink(amount);
                    }
                    if (stack.isEmpty())
                        continue;

                    stacks.add(stack);
                }
            }
        } else {
            ItemStack out = recipe.getResultItem().copy();
            stacks = ItemHelper.multipliedOutput(stackIn, out);
        }

        return stacks;
    }

    public static class CustomFanRecipeWrapper extends RecipeWrapper {

        public CustomFanRecipeWrapper() {
            super(new ItemStackHandler(1));
        }

    }

    public static class CustomAirCurrentSegment {

        public FluidOrBlock type;
        public int startOffset;
        public int endOffset;

        public CustomAirCurrentSegment() {
        }

    }

}
