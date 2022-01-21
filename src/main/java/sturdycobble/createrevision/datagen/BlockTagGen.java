package sturdycobble.createrevision.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.init.ModTags;

import javax.annotation.Nullable;

public class BlockTagGen extends BlockTagsProvider {

    public BlockTagGen(DataGenerator generator, @Nullable ExistingFileHelper helper) {
        super(generator, CreateRevision.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.CUSTOM_FAN_SOURCE_BLOCK).add(Blocks.WITHER_ROSE, Blocks.POWDER_SNOW);
    }

}