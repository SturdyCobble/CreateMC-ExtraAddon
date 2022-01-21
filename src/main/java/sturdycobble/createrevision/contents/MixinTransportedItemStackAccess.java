package sturdycobble.createrevision.contents;

import sturdycobble.createrevision.utils.FluidOrBlock;

public interface MixinTransportedItemStackAccess {

    FluidOrBlock getCustomProcessType();

    void setCustomProcessType(FluidOrBlock type);

    int getCustomProcessTime();

    void setCustomProcessTime(int time);

}
