package com.sturdycobble.createrevision.contents.heat;

import java.util.Map;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IHeatableTileEntity extends ICapabilityProvider {
	
	public abstract void markConnection();
	public abstract Map<IHeatableTileEntity, Long> findNeighborNode();
	public abstract void updateConnection();
	public abstract boolean isNode();

}
