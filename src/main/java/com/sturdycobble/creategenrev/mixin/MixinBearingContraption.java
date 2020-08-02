package com.sturdycobble.creategenrev.mixin;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.BearingContraption;
import com.sturdycobble.creategenrev.init.ModTags.ModBlockTags;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;

@Mixin(BearingContraption.class)
public abstract class MixinBearingContraption extends Contraption{
	@Shadow
	protected int sailBlocks;
	
	@Overwrite(remap = false)
	@Override
	public void add(BlockPos pos, Pair<BlockInfo, TileEntity> capture) {
		BlockPos localPos = pos.subtract(anchor);
		if (!blocks.containsKey(localPos) && ModBlockTags.WINDMILL_SAILS_REV.matches(capture.getKey().state))
			sailBlocks++;
		super.add(pos, capture);
	}
}
