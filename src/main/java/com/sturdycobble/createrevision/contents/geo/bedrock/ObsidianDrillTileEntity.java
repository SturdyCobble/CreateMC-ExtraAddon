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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class ObsidianDrillTileEntity extends KineticTileEntity {

	private final int breakTime = 1280000;
	private int breakProgress;
	private BlockPos breakingPos;
	private boolean isRunning;
	
	public final ItemStackHandler inventory;
	
	public ObsidianDrillTileEntity() {
		super(ModTileEntityTypes.OBSIDIAN_DRILL.get());
		inventory = new ItemStackHandler(1);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("Progress", breakProgress);
		if (breakingPos != null)
			compound.put("Breaking", NBTUtil.writeBlockPos(breakingPos));
		compound.put("Inventory", inventory.serializeNBT());
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		breakProgress = compound.getInt("Progress");
		if (compound.contains("Breaking"))
			breakingPos = NBTUtil.readBlockPos(compound.getCompound("Breaking"));
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		super.read(compound);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return LazyOptional.of(ObsidianDrillInventoryHandler::new).cast();
		
		return super.getCapability(cap, side);
	}
	
	@Override
	public float calculateStressApplied() {
		return 512;
	}
	
	@Override
	public void tick() {
		if (world.isRemote)
			return;
		if (getSpeed() == 0)
			return;
		
		if (isRunning) {
			breakProgress = (int) ( breakProgress + Math.abs(getSpeed()*0.2) );

		}
		
		if ( breakProgress > breakTime ) {
			breakProgress = 0;
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
		ItemHandlerHelper.insertItemStacked(inventory, outputItem, false);
		sendData();
		markDirty();
	}
	
	private class ObsidianDrillInventoryHandler extends CombinedInvWrapper {

		public ObsidianDrillInventoryHandler() {
			super(inventory);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (inventory == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return super.extractItem(slot, amount, simulate);
		}


	}



}