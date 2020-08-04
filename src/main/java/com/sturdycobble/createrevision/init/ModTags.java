package com.sturdycobble.createrevision.init;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.util.nullness.NonNullFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

/**
 * Tag Registrations.
 * Slightly Modified Version of com.simibubi.create.AllTags
 * 
 * @author SturdyCobble
 * 
 */
public class ModTags{
	/*private static final CreateRegistrate REGISTRATE = Create.registrate()
			.itemGroup(() -> Create.baseCreativeTab);*/

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
		String tagName) {
		return b -> b.tag(forgeBlockTag(tagName))
			.item()
			.tag(forgeItemTag(tagName));
	}

	public static Tag<Block> forgeBlockTag(String name) {
		return forgeTag(BlockTags.getCollection(), name);
	}

	public static Tag<Item> forgeItemTag(String name) {
		return forgeTag(ItemTags.getCollection(), name);
	}

	public static <T> Tag<T> forgeTag(TagCollection<T> collection, String name) {
		return tag(collection, "forge", name);
	}

	public static <T> Tag<T> tag(TagCollection<T> collection, String domain, String name) {
		return collection.getOrCreate(new ResourceLocation(domain, name));
	}



	public static enum ModBlockTags {
		WINDMILL_SAILS_REV 
	;

		public Tag<Block> tag;
		
		
		private ModBlockTags() {
			this.tag = new BlockTags.Wrapper(
					new ResourceLocation("create_generator_revision", Lang.asId(name()))); 
		}

		public boolean matches(BlockState block) {
			return this.tag.contains(block.getBlock());
		}
			
	}

	public static void register() {

	}
}
