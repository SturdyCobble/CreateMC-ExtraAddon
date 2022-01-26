package sturdycobble.createrevision.api.depot_recipe;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.utils.RGBColor;

public interface BeaconRecipe<C extends Container> {

    boolean matches(C container, Level world, int beaconLevel, RGBColor color);

}
