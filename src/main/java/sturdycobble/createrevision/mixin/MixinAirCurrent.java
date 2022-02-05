package sturdycobble.createrevision.mixin;

import com.simibubi.create.content.contraptions.components.fan.AirCurrent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sturdycobble.createrevision.contents.custom_fan.CustomAirCurrent;

import java.util.List;

@Mixin(AirCurrent.class)
public abstract class MixinAirCurrent {

    public CustomAirCurrent customCurrent;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructorHead(CallbackInfo ci) {
        customCurrent = new CustomAirCurrent((AirCurrent) (Object) this);
    }

    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    public void onTick(CallbackInfo ci) {
        customCurrent.tickAffectedHandlers();
        customCurrent.tickAffectedEntities(((AirCurrent) (Object) this).source.getAirCurrentWorld(), ((AirCurrent) (Object) this).direction);
    }

    @Inject(method = "rebuild", at = @At("RETURN"), remap = false)
    protected void onRebuild(CallbackInfo ci) {
        customCurrent.rebuild();
    }

}
