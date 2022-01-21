package sturdycobble.createrevision.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import sturdycobble.createrevision.CreateRevision;

public class ModTags {

    public static final Tags.IOptionalNamedTag<Block> CUSTOM_FAN_SOURCE_BLOCK =
            BlockTags.createOptional(new ResourceLocation(CreateRevision.MODID, "fan_source"));
    public static final Tags.IOptionalNamedTag<Fluid> CUSTOM_FAN_SOURCE_FLUID =
            FluidTags.createOptional(new ResourceLocation(CreateRevision.MODID, "fan_source"));

    private ModTags() {
    }

}
