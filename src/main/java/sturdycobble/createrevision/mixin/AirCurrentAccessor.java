package sturdycobble.createrevision.mixin;

import com.simibubi.create.content.contraptions.components.fan.AirCurrent;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AirCurrent.class)
public interface AirCurrentAccessor {

    @Accessor("caughtEntities")
    List<Entity> getCaughtEntities();

}
