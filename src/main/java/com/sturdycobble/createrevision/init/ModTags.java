package com.sturdycobble.createrevision.init;

import com.simibubi.create.foundation.utility.Lang;
import com.sturdycobble.createrevision.CreateRevision;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {

	public static enum ModBlockTags {
		;

		public Tag<Block> tag;

		private ModBlockTags() {
			this.tag = new BlockTags.Wrapper(new ResourceLocation(CreateRevision.MODID, Lang.asId(name())));
		}

		public boolean matches(BlockState block) {
			return this.tag.contains(block.getBlock());
		}

	}

	public static void register() {
	}

}
