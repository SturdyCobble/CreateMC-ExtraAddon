package sturdycobble.createrevision.contents.reinforced_depot;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.api.depot_recipe.SimpleBeaconRecipe;
import sturdycobble.createrevision.init.ModBlockEntityTypes;
import sturdycobble.createrevision.init.ModConfigs;
import sturdycobble.createrevision.init.ModRecipeTypes;
import sturdycobble.createrevision.mixin.BeaconBlockEntityAccessor;
import sturdycobble.createrevision.utils.RGBColor;

import java.util.List;
import java.util.Optional;

public class ReinforcedDepotBlockEntity extends SmartTileEntity {

    private static final ReinforcedDepotRecipeWrapper REINFORCED_DEPOT_RECIPE_WRAPPER = new ReinforcedDepotRecipeWrapper();
    ReinforcedDepotBehaviour depotBehaviour;
    int processingTime = -1;
    CustomRecipeInfo currentRecipeInfo = CustomRecipeInfo.empty();
    int heightAboveBeacon = 0;

    public ReinforcedDepotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.REINFORCED_DEPOT.get(), pos, state);
    }

    public void findBeacon() {
        BlockPos.MutableBlockPos testPos = worldPosition.mutable();
        int minY = level.getMinBuildHeight();
        int testHeight = 0;
        while (testPos.getY() > minY) {
            testPos.move(Direction.DOWN);
            testHeight++;

            BlockState currentState = level.getBlockState(testPos);
            if (currentState.getLightBlock(level, testPos) >= 15 && currentState.getBlock() != Blocks.BEDROCK)
                break;
            if (currentState.getBlock().equals(Blocks.BEACON)) {
                BlockEntity currentTile = level.getBlockEntity(testPos);
                if (!(currentTile instanceof BeaconBlockEntity beaconTile))
                    break;

                List<BeaconBlockEntity.BeaconBeamSection> beamSections = beaconTile.getBeamSections();
                if (!(beamSections.isEmpty())) {
                    heightAboveBeacon = testHeight;
                }
                break;
            }
        }
    }

    public Optional<BeaconBlockEntity> getBeacon() {
        if (heightAboveBeacon <= 0)
            return Optional.empty();
        BlockEntity tile = level.getBlockEntity(worldPosition.below(heightAboveBeacon));
        if (tile instanceof BeaconBlockEntity && tile != null) {
            BeaconBlockEntity beaconTile = (BeaconBlockEntity) tile;
            if (beaconTile.getBeamSections().size() == 0)
                return Optional.empty();

            return Optional.of((BeaconBlockEntity) tile);
        }
        return Optional.empty();
    }

    public int getBeaconLevel() {
        if (getBeacon().isPresent()) {
            BeaconBlockEntity beaconTile = getBeacon().get();
            return ((BeaconBlockEntityAccessor) beaconTile).getPower();
        }
        return -1;
    }

    public RGBColor getBeamColor() {
        if (getBeacon().isPresent()) {
            BeaconBlockEntity beaconTile = getBeacon().get();
            int testHeight = 0;
            for (BeaconBlockEntity.BeaconBeamSection beam : beaconTile.getBeamSections()) {
                testHeight += beam.getHeight();
                if (testHeight > heightAboveBeacon)
                    return new RGBColor(beam.getColor());
            }
        }
        return RGBColor.white();
    }

    public boolean tryBeaconRecipe(TransportedItemStack heldItem) {
        ItemStack previousItem = heldItem.stack;

        if (getBeacon().isPresent()) {
            REINFORCED_DEPOT_RECIPE_WRAPPER.setItem(0, previousItem);
            int power = getBeaconLevel();
            RGBColor color = getBeamColor();
            Optional<SimpleBeaconRecipe> recipe = ModRecipeTypes.findBeaconRecipe(ModRecipeTypes.BEACON_DEPOT_RECIPE, REINFORCED_DEPOT_RECIPE_WRAPPER, this.getLevel(), power, color);
            if (recipe.isPresent())
                return applyBeaconRecipe(recipe.get(), heldItem);
            processingTime = -1;
        } else {
            findBeacon();
        }
        currentRecipeInfo.clear();
        return false;
    }

    public boolean applyBeaconRecipe(SimpleBeaconRecipe recipe, TransportedItemStack heldItem) {
        int stackSize = heldItem.stack.getCount();
        processingTime--;

        CustomRecipeInfo newRecipeInfo = new CustomRecipeInfo(recipe);

        if (!newRecipeInfo.equals(currentRecipeInfo)) {
            processingTime = ModConfigs.getBeaconDepotProcessingTime();
            currentRecipeInfo = newRecipeInfo;
        }

        if (processingTime == 0 && stackSize > 0 && recipe instanceof BeaconDepotRecipe) {
            List<ItemStack> rollResults = recipe.rollResults();
            int outputSlotSize = depotBehaviour.processingOutputBuffer.getSlots();

            if (rollResults.size() > 0) {
                if (!depotBehaviour.processingOutputBuffer.insertItem(outputSlotSize - 1, rollResults.get(0), true).equals(ItemStack.EMPTY)) {
                    processingTime = 0;
                    return true;
                }
            }

            ItemStack overflow;
            for (ItemStack resultStack : rollResults) {
                overflow = resultStack;
                for (int k = 0; k < outputSlotSize; k++) {
                    overflow = depotBehaviour.processingOutputBuffer.insertItem(k, overflow, false);
                    if (overflow == ItemStack.EMPTY)
                        break;
                }
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY() + 0.1, worldPosition.getZ(), overflow));
            }
            heldItem.stack.shrink(1);

            processingTime = -1;
            currentRecipeInfo.clear();
            return true;
        }

        return false;
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("ProcessTime", processingTime);
        tag.putString("RecipeType", currentRecipeInfo.toString());
    }

    @Override
    public void writeSafe(CompoundTag tag, boolean clientPacket) {
        super.writeSafe(tag, clientPacket);
        tag.putInt("ProcessTime", processingTime);
        tag.putString("RecipeType", currentRecipeInfo.toString());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        processingTime = tag.getInt("ProcessTime");
        currentRecipeInfo.fromString(tag.getString("RecipeType"));
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        behaviours.add(depotBehaviour = new ReinforcedDepotBehaviour(this));
        depotBehaviour.addSubBehaviours(behaviours);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return depotBehaviour.getItemCapability(cap, side);
        return super.getCapability(cap, side);
    }

    public static class ReinforcedDepotRecipeWrapper extends RecipeWrapper {

        public ReinforcedDepotRecipeWrapper() {
            super(new ItemStackHandler(1));
        }

    }

    private static class CustomRecipeInfo {

        String recipeInfo;

        public CustomRecipeInfo(Recipe<?> recipe) {
            recipeInfo = recipe.getType().toString();

            for (Ingredient ingredient : recipe.getIngredients()) {
                for (ItemStack stack : ingredient.getItems()) {
                    recipeInfo += (stack.getCount() + stack.getItem().toString());
                }
            }
        }

        public CustomRecipeInfo() {
            recipeInfo = "";
        }

        public static CustomRecipeInfo empty() {
            return new CustomRecipeInfo();
        }

        public String toString() {
            return recipeInfo;
        }

        public void fromString(String input) {
            recipeInfo = input;
        }

        public boolean equals(CustomRecipeInfo obj) {
            return obj.toString().equals(recipeInfo);
        }

        public void clear() {
            recipeInfo = "";
        }

    }

}
