package sturdycobble.createrevision.contents.reinforced_depot;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ReinforcedDepotItemHandler implements IItemHandler {

    private final ReinforcedDepotBehaviour te;

    public ReinforcedDepotItemHandler(ReinforcedDepotBehaviour te) {
        this.te = te;
    }

    public int getSlots() {
        return 9;
    }

    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? this.te.getHeldItemStack() : this.te.processingOutputBuffer.getStackInSlot(slot - 1);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot != 0) {
            return stack;
        } else if (!this.te.getHeldItemStack().isEmpty() && !this.te.canMergeItems()) {
            return stack;
        } else if (!this.te.isOutputEmpty() && !this.te.canMergeItems()) {
            return stack;
        } else {
            ItemStack remainder = this.te.insert(new TransportedItemStack(stack), simulate);
            if (!simulate && remainder != stack) {
                this.te.tileEntity.notifyUpdate();
            }

            return remainder;
        }
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0) {
            return this.te.processingOutputBuffer.extractItem(slot - 1, amount, simulate);
        } else {
            TransportedItemStack held = this.te.heldItem;
            if (held == null) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stack = held.stack.copy();
                ItemStack extracted = stack.split(amount);
                if (!simulate) {
                    this.te.heldItem.stack = stack;
                    if (stack.isEmpty()) {
                        this.te.heldItem = null;
                    }

                    this.te.tileEntity.notifyUpdate();
                }

                return extracted;
            }
        }
    }

    public int getSlotLimit(int slot) {
        return slot == 0 ? this.te.maxStackSize.get() : 64;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0;
    }

}
