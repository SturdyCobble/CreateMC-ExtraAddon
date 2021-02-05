package com.sturdycobble.createrevision.contents.heat;

import static net.minecraft.state.properties.BlockStateProperties.FACING;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.utility.recipe.RecipeConditions;
import com.simibubi.create.foundation.utility.recipe.RecipeFinder;
import com.sturdycobble.createrevision.api.heat.CapabilityHeat;
import com.sturdycobble.createrevision.api.heat.HeatContainer;
import com.sturdycobble.createrevision.api.heat.HeatNode;
import com.sturdycobble.createrevision.api.heat.SimpleWritableHeatContainer;
import com.sturdycobble.createrevision.init.ModConfigs;
import com.sturdycobble.createrevision.init.ModRecipeTypes;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatExchangerTileEntity extends SyncedTileEntity implements ITickableTileEntity {

	private static final Object heatRecipesKey = new Object();

	private static final double BURNER_HEAT_KINDLED = 6;
	private static final double BURNER_HEAT_SEETHING = 15;
	private static final double MAX_BURNER_KINDLED_TEMP = 500;
	private static final double MAX_BURNER_SEETHING_TEMP = 1500;

	private final SimpleWritableHeatContainer heatContainer = new SimpleWritableHeatContainer() {
		@Override
		public double getCapacity() {
			return ModConfigs.getHeatPipeHeatCapacity();
		}

		@Override
		public double getConductivity() {
			return ModConfigs.getHeatPipeConductivity();
		}
	};

	private final HeatNode node = new HeatNode(this, heatContainer);

	private double heatExchanged = 0;
	private int exchangeTime = 0;

	public HeatExchangerTileEntity() {
		super(ModTileEntityTypes.HEAT_EXCHANGER.get());
	}

	private boolean isValidSide(@Nullable Direction side) {
		return side == null ? true : side != getBlockState().get(FACING).getOpposite();
	}

	public void updateAllNeighbors(BlockState state) {
		for (Direction direction : Direction.values()) {
			if (isValidSide(direction)) {
				node.getConnection(direction).markForUpdate();
			}
		}
	}

	@Override
	public void tick() {
		if (world.getWorldInfo().getGameTime() % HeatNode.HEAT_UPDATE_TICK == 0) {
			setPowerWithInteraction(heatContainer.getTemp());
			if (exchangeTime > 0) {
				heatContainer.addHeat(heatExchanged);
				exchangeTime--;
			}
			node.updateTemp();
			markDirty();
		}
	}

	private List<? extends IRecipe<?>> getRecipes(Block block) {
		List<IRecipe<?>> startedSearch = RecipeFinder.get(heatRecipesKey, world,
				RecipeConditions.isOfType(ModRecipeTypes.HEAT_EXCHANGER.getType()));
		return startedSearch.stream().filter(r -> {
			return ((r instanceof HeatExchangerRecipe<?>) ? ((HeatExchangerRecipe<?>) r).getInputBlock().equals(block)
					: false);
		}).collect(Collectors.toList());
	}

	public void setPowerWithInteraction(double temp) {
		Direction facing = getBlockState().get(FACING);
		BlockPos offsetPos = pos.offset(facing.getOpposite());
		Block offsetBlock = world.getBlockState(offsetPos).getBlock();

		if (offsetBlock.equals(AllBlocks.BLAZE_BURNER.get()) && facing.equals(Direction.UP)) {
			HeatLevel burnerLevel = getHeatLevelOf(world.getBlockState(offsetPos));
			if (burnerLevel == HeatLevel.KINDLED && temp < MAX_BURNER_KINDLED_TEMP) {
				heatExchanged = BURNER_HEAT_KINDLED;
				exchangeTime = HeatNode.HEAT_UPDATE_TICK;
			} else if (burnerLevel == HeatLevel.SEETHING && temp < MAX_BURNER_SEETHING_TEMP) {
				heatExchanged = BURNER_HEAT_SEETHING;
				exchangeTime = HeatNode.HEAT_UPDATE_TICK;
			}
		}

		List<? extends IRecipe<?>> recipes = getRecipes(offsetBlock);
		if (recipes.isEmpty())
			return;
		for (IRecipe<?> recipe : recipes) {
			HeatExchangerRecipe<?> heatRecipe = (HeatExchangerRecipe<?>) recipe;
			if (heatRecipe.isValidTemp(temp)) {
				Block resultBlock = heatRecipe.getOutputBlock();
				world.setBlockState(offsetPos, resultBlock.getDefaultState());
				heatExchanged = heatRecipe.getHeatProduction();
				exchangeTime = heatRecipe.getDuration();
				break;
			}
		}
	}

	private static HeatLevel getHeatLevelOf(BlockState state) {
		if (state.has(BlazeBurnerBlock.HEAT_LEVEL))
			return state.get(BlazeBurnerBlock.HEAT_LEVEL);
		return AllTags.AllBlockTags.FAN_HEATERS.matches(state) ? HeatLevel.SMOULDERING : HeatLevel.NONE;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag.put("heat", heatContainer.serializeNBT());
		return super.write(tag);
	}

	@Override
	public void read(CompoundNBT tag) {
		heatContainer.deserializeNBT(tag.getCompound("heat"));
		super.read(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();

		nbt.put("heat", heatContainer.serializeNBT());
		return nbt;
	}

	@Override
	public void handleUpdateTag(CompoundNBT nbt) {
		super.handleUpdateTag(nbt);
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();

		nbt.put("heat", heatContainer.serializeNBT());
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		heatContainer.deserializeNBT(nbt.getCompound("heat"));
	}

	private final LazyOptional<HeatContainer> heatContainerCap = LazyOptional.of(() -> heatContainer);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityHeat.HEAT_CAPABILITY) {
			return isValidSide(side) ? heatContainerCap.cast() : LazyOptional.empty();
		}
		return super.getCapability(cap, side);
	}

}
