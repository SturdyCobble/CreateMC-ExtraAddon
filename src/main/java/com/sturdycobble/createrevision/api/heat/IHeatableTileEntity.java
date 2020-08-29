package com.sturdycobble.createrevision.api.heat;

import java.util.Map;

import com.sturdycobble.createrevision.utils.HeatUtils.FacingDistance;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IHeatableTileEntity extends ICapabilityProvider {
	
	public abstract void markConnection();
	public abstract Map<IHeatableTileEntity, FacingDistance> findNeighborNode();
	public abstract void updateConnection();
	public abstract boolean isNode();
	public abstract Map<IHeatableTileEntity, FacingDistance> getNeighborMap();
	
}
