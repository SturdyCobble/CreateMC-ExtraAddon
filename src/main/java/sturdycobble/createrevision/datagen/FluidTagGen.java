package sturdycobble.createrevision.datagen;

import com.simibubi.create.AllFluids;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import sturdycobble.createrevision.CreateRevision;
import sturdycobble.createrevision.init.ModTags;

import javax.annotation.Nullable;

public class FluidTagGen extends FluidTagsProvider {

    public FluidTagGen(DataGenerator generator, @Nullable ExistingFileHelper helper) {
        super(generator, CreateRevision.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.CUSTOM_FAN_SOURCE_FLUID).add(AllFluids.HONEY.get().getSource());
    }

}