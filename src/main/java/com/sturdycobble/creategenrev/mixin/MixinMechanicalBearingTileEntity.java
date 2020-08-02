package com.sturdycobble.creategenrev.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.IBearingTileEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

@Mixin(MechanicalBearingTileEntity.class)
public abstract class MixinMechanicalBearingTileEntity  extends GeneratingKineticTileEntity implements IBearingTileEntity {

	@Shadow
	protected boolean isWindmill;
	protected boolean running;
	protected ContraptionEntity movedContraption;
	protected float lastGeneratedSpeed;

	public MixinMechanicalBearingTileEntity(TileEntityType<? extends MixinMechanicalBearingTileEntity> type) {
		super(type);
	}
	
	@Overwrite(remap = false)
	public float getGeneratedSpeed() {
		if (!running || !isWindmill)
			return 0;
		if (movedContraption == null)
			return lastGeneratedSpeed;
		Biome biome = world.getBiome(pos);
		int biomeDivisor =  biome.getCategory() == Biome.Category.OCEAN ? 1 : 2;
		int heightDivisor = pos.getY() > 60 ? 1 : 4;
		int spaceDivisor = isClearSpace() == true ? 5 : 1;
		int sails = ((BearingContraption) movedContraption.getContraption()).getSailBlocks() / (8*biomeDivisor*heightDivisor*spaceDivisor);
		return MathHelper.clamp(sails, 1, 16);
	}
	
	private boolean isClearSpace() {
		int orgX = pos.getX();
		int orgY = pos.getY();
		int orgZ = pos.getZ();
		
		for (int parmX = orgX - 10; parmX <= orgX + 10; parmX++ ) {
			for (int parmY = Math.max(orgY-10, 0) ; parmY <= Math.min(orgY +10, 255); parmY++) {
				for (int parmZ = orgZ - 10; parmZ <= orgZ + 10; parmZ++) {
					BlockPos focusedPos = new BlockPos(parmX, parmY, parmZ);
					if (parmX == orgX && parmY == orgY && parmZ == orgZ) continue;
					if (world.getBlockState(focusedPos).getBlock() == AllBlocks.MECHANICAL_BEARING.get()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
