package vazkii.morphtool.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import vazkii.morphtool.MorphToolMod;
import vazkii.morphtool.common.MorphToolRegistries;

import java.util.List;

/**
 * This wraps the ItemStack that is added to the Morph Tool and holds some additional information about the stack.
 *
 * @param stack         The Stack added to the MorphTool.
 * @param modId         This is the mod the wrapped stack belongs to.
 * @param ogDisplayName The display name that was present on the item when it got added to the Tool. This should
 *                      never change. --> Renaming a morphed Item in the anvil should currently lose the name when
 *                      turned into the morph tool again. (Might change later)
 */
public record MorphingStackWrapper(ItemStack stack, String modId, Component ogDisplayName, boolean isCustomName) {

	public static final Codec<MorphingStackWrapper> CODEC = RecordCodecBuilder.create(obj -> obj.group(
			ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(i -> i.stack),
			Codec.STRING.fieldOf("mod_id").forGetter(i -> i.modId),
			ComponentSerialization.FLAT_CODEC.fieldOf("og_display_name").forGetter(i -> i.ogDisplayName),
			Codec.BOOL.fieldOf("is_custom_name").forGetter(i -> i.isCustomName)
	).apply(obj, MorphingStackWrapper::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, MorphingStackWrapper> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

	public MorphingStackWrapper updateStack(ItemStack newStack) {
		if (newStack.isEmpty()) {
			MorphToolMod.LOGGER.error("Unable to modify {} in MorphingStackWrapper because the given stack is empty", this.stack.getItem());
			return this;
		}
		if (!ItemStack.isSameItem(stack, newStack)) {
			MorphToolMod.LOGGER.error("Unable to modify {} in MorphingStackWrapper because the given item [ {} ] is not the same", this.stack.getItem(), newStack.getItem());
			return this;
		}

		var currentName = newStack.getHoverName();
		if (currentName.getContents() instanceof TranslatableContents contents) {
			var args = contents.getArgs();
			if (contents.getKey().equals("morphtool.sudo_name")) {
				currentName = (Component) args[0]; //This is the part inside "Morphing Tool ()"

			}

		}

		var newDisplayName = currentName.copy().setStyle(this.ogDisplayName.getStyle());

		newStack.remove(MorphToolRegistries.MORPH_DATA);

		if (this.isCustomName) {
			newStack.set(DataComponents.CUSTOM_NAME, newDisplayName);
		} else {
			newStack.remove(DataComponents.CUSTOM_NAME);
			newStack.set(DataComponents.ITEM_NAME, newDisplayName);
		}

		return new MorphingStackWrapper(newStack, this.modId, newDisplayName, this.isCustomName);
	}

	public static boolean listMatches(List<MorphingStackWrapper> first, List<MorphingStackWrapper> second) {
		if (first.size() != second.size()) {
			return false;
		} else {
			for (int i = 0; i < first.size(); i++) {
				if (!matches(first.get(i), second.get(i))) {
					return false;
				}
			}

			return true;
		}
	}

	public static boolean matches(MorphingStackWrapper first, MorphingStackWrapper second) {
		if (first == second) {
			return true;
		} else {
			return ItemStack.isSameItemSameComponents(first.stack, second.stack) && first.modId.equals(second.modId) && first.ogDisplayName.equals(second.ogDisplayName);
		}
	}

	@Override
	public String toString() {
		return "MorphingStackWrapper" + this.stack;
	}

}
