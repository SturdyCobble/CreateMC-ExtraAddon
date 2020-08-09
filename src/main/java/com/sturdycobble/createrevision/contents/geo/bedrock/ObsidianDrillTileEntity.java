package com.sturdycobble.createrevision.contents.geo.bedrock;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.sturdycobble.createrevision.init.ModItems;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ObsidianDrillTileEntity extends KineticTileEntity {

	private final int BREAK_TIME = 1280000;
	private int breakTimeLeft;
	private BlockPos breakingPos;
	private boolean isRunning;
	
	
	public final ItemStackHandler outputInventory = new ItemStackHandler(1);
	
	protected LazyOptional<IItemHandlerModifiable> inventory =
			LazyOptional.of(() -> new ObsidianDrillInventoryHandler(outputInventory));
	
	public ObsidianDrillTileEntity() {
		super(ModTileEntityTypes.OBSIDIAN_DRILL.get());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("BreakTimeLeft", breakTimeLeft);
		if (breakingPos != null)
			compound.put("Breaking", NBTUtil.writeBlockPos(breakingPos));
		compound.put("Inventory", outputInventory.serializeNBT());
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		breakTimeLeft = compound.getInt("BreakTimeLeft");
		if (compound.contains("Breaking"))
			breakingPos = NBTUtil.readBlockPos(compound.getCompound("Breaking"));
		outputInventory.deserializeNBT(compound.getCompound("Inventory"));
		super.read(compound);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return inventory.cast();
		
		return super.getCapability(cap, side);
	}
	
	@Override
	public float calculateStressApplied() {
		return 512;
	}
	
	@Override
	public void tick() {
		if (world.isRemote || getSpeed() == 0)
			return;
		
		if (isRunning) {
			breakTimeLeft = (int) ( breakTimeLeft - Math.abs(getSpeed()*0.2) );

		}
		
		if ( breakTimeLeft < 0 ) {
			breakTimeLeft = BREAK_TIME;
			getPowder();
		}
	}
	
	public void updateDrill(Direction facing) {
		breakingPos = pos.offset(facing);
		boolean shouldRun = world.isBlockPowered(pos) 
				&& world.getBlockState(breakingPos).getBlock() == Blocks.BEDROCK;
		if (isRunning == shouldRun) {
			return;
		}
		isRunning = shouldRun;
	}
	
	private void getPowder() {
		ItemStack outputItem = new ItemStack(ModItems.BEDROCK_POWDER.get());
		ItemHandlerHelper.insertItemStacked(outputInventory, outputItem, false);
		sendData();
		markDirty();
	}
	
	private class ObsidianDrillInventoryHandler extends CombinedInvWrapper {

		public ObsidianDrillInventoryHandler(ItemStackHandler outputInventory) {
			super(outputInventory);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (outputInventory == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return super.extractItem(slot, amount, simulate);
		}
	}

}