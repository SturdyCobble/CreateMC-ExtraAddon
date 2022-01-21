package sturdycobble.createrevision.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidOrBlock {

    public Block block;
    public Fluid fluid;

    public FluidOrBlock(Block block) {
        this.block = block;
        this.fluid = Fluids.EMPTY;
    }

    public FluidOrBlock(Fluid fluid) {
        this.block = Blocks.AIR;
        this.fluid = fluid instanceof FlowingFluid ? ((FlowingFluid) fluid).getSource() : fluid;
    }

    public static FluidOrBlock empty() {
        return new FluidOrBlock(Blocks.AIR);
    }

    public static boolean isFluidId(String id) {
        return id.startsWith("Fluid");
    }

    public static FluidOrBlock fromString(String id) {
        return isFluidId(id) ? new FluidOrBlock(ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(id)))
                : new FluidOrBlock(ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(id)));
    }

    public boolean is(Block block) {
        return this.fluid.equals(Fluids.EMPTY) && this.block.equals(block);
    }

    public boolean is(Fluid fluid) {
        Fluid sourceFluid = fluid instanceof FlowingFluid ? ((FlowingFluid) fluid).getSource() : fluid;
        return this.block.equals(Blocks.AIR) && this.fluid.equals(sourceFluid);
    }

    public boolean isEmpty() {
        return this.fluid.equals(Fluids.EMPTY) && this.block.equals(Blocks.AIR);
    }

    public String toString() {
        return isBlock() ? block.toString() : "Fluid{" + fluid.getRegistryName() + "}";
    }

    public boolean isBlock() {
        return !block.equals(Blocks.AIR) && fluid.equals(Fluids.EMPTY);
    }

    @Override
    public boolean equals(Object type) {
        if (type instanceof FluidOrBlock) {
            return isBlock() ? is(((FluidOrBlock) type).block) : is(((FluidOrBlock) type).fluid);
        } else if (type instanceof Fluid) {
            return is((Fluid) type);
        } else if (type instanceof Block) {
            return is((Block) type);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return isBlock() ? block.hashCode() : 31 * fluid.hashCode();
    }

}
