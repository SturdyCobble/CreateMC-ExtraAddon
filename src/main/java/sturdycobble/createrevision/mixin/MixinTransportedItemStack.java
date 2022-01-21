package sturdycobble.createrevision.mixin;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sturdycobble.createrevision.contents.MixinTransportedItemStackAccess;
import sturdycobble.createrevision.utils.FluidOrBlock;

@Mixin(TransportedItemStack.class)
public class MixinTransportedItemStack implements MixinTransportedItemStackAccess {

    public FluidOrBlock customProcessedBy;
    public int customProcessingTime;

    @Inject(method = "getSimilar", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false, cancellable = true)
    public void onGetSimilar(CallbackInfoReturnable<TransportedItemStack> cir) {
        MixinTransportedItemStack copy = (MixinTransportedItemStack) (Object) cir.getReturnValue();
        copy.customProcessedBy = this.customProcessedBy;
        copy.customProcessingTime = this.customProcessingTime;
        cir.setReturnValue((TransportedItemStack) (Object) copy);
    }

    @Override
    public FluidOrBlock getCustomProcessType() {
        return customProcessedBy != null ? customProcessedBy : FluidOrBlock.empty();
    }

    @Override
    public void setCustomProcessType(FluidOrBlock type) {
        customProcessedBy = type;
    }

    @Override
    public int getCustomProcessTime() {
        return customProcessingTime;
    }

    @Override
    public void setCustomProcessTime(int time) {
        customProcessingTime = time;
    }

}
