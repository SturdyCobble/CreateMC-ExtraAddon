package com.sturdycobble.createrevision.contents.geo.bedrock;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.sturdycobble.createrevision.init.ModTileEntityTypes;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.ItemStackHandler;

public class BedrockAnvilTileEntity extends KineticTileEntity {

	private final int anvilTime = 3000;
	private int anvilProgress;
	private boolean isRunning = true;
	private int runningTicks;

	public final ItemStackHandler inventory;

	public BedrockAnvilTileEntity() {
		super(ModTileEntityTypes.BEDROCK_ANVIL.get());
		inventory = new ItemStackHandler(1);
		anvilProgress = 0;
		runningTicks = 0;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("Progress", anvilProgress);
		compound.put("Inventory", inventory.serializeNBT());
		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		anvilProgress = compound.getInt("Progress");
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		super.read(compound);
	}

	@Override
	public float calculateStressApplied() {
		return 192;
	}

	@Override
	public void tick() {
		runningTicks++;
		if (runningTicks > 80)
			runningTicks = 0;
	}

	public float getRenderedPressOffset(float partialTicks) {
		if (Math.abs(getSpeed()) > 0) {
			if (runningTicks < 40) {
				return MathHelper.clamp(0.05f*runningTicks + partialTicks, 0, 0.5f);
			}
			if (runningTicks > 40) {
				return MathHelper.clamp(((0.5f - 0.05f*runningTicks*runningTicks) - partialTicks), 0, 0.5f);
			}
		}
		return 0;
	}

}
