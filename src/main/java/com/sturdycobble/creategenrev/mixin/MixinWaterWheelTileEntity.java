package com.sturdycobble.creategenrev.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.biome.Biome;

@Mixin(WaterWheelTileEntity.class)
public abstract class MixinWaterWheelTileEntity extends GeneratingKineticTileEntity{
	
	@Shadow
	private Map<Direction, Float> flows;
	
	public MixinWaterWheelTileEntity(TileEntityType<? extends WaterWheelTileEntity> type) {
		super(type);
	}

	/**@author StdCobble**/
	@Overwrite(remap = false)
	public float getGeneratedSpeed() {
		float speed = 0;
		for (Float f : flows.values())
			speed += f;
		Biome biome = world.getBiome(pos);
		return biome.getCategory() == Biome.Category.RIVER && Math.abs(pos.getY() - 63) < 1 && speed != 0 ? Math.signum(speed)*12 : speed;
	}

}
