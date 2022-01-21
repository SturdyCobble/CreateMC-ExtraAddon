package sturdycobble.createrevision.api.fan_recipe;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import sturdycobble.createrevision.utils.FluidOrBlock;

public interface CustomFluidOrBlockRecipe<C extends Container> {

    boolean matches(C container, Level world, FluidOrBlock type);

}
