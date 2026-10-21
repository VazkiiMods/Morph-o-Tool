package vazkii.morphtool.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import vazkii.morphtool.MorphToolMod;
import vazkii.morphtool.common.MorphToolRegistries;

import java.util.List;

public class MorphingStackWrapper {
    public static final Codec<MorphingStackWrapper> CODEC = RecordCodecBuilder.create(obj -> obj.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(i -> i.stack),
            Codec.STRING.fieldOf("mod_id").forGetter(i -> i.modId),
            ComponentSerialization.FLAT_CODEC.fieldOf("og_display_name").forGetter(i -> i.ogDisplayName)
            ).apply(obj, MorphingStackWrapper::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MorphingStackWrapper> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final ItemStack stack;
    private final String modId;
    private final Component ogDisplayName;

    /**
     * This wraps the ItemStack that is added to the Morph Tool and holds some additional information about the stack.
     * @param stack The Stack added to the MorphTool.
     * @param modId This is the mod the wrapped stack belongs to.
     * @param ogDisplayName The display name that was present on the item when it got added to the Tool. This should never change. --> Renaming a morphed Item in the anvil should currently lose the name when turned into the morph tool again. (Might change later)
     */
    public MorphingStackWrapper(ItemStack stack, String modId, Component ogDisplayName) {
        this.stack = stack;
        this.modId = modId;
        this.ogDisplayName = ogDisplayName;
    }

    public ItemStack getStack() {
        return stack;
    }

    public String getModId() {
        return modId;
    }

    public Component getOgDisplayName() {
        return ogDisplayName;
    }

    public MorphingStackWrapper updateStack(ItemStack newStack) {
        if (newStack.isEmpty()) {
            MorphToolMod.LOGGER.error("Unable to modify {} in MorphingStackWrapper because the given stack is empty", this.stack.getItem());
            return this;
        }
        if (!ItemStack.isSameItem(stack, newStack)) {
            MorphToolMod.LOGGER.error("Unable to modify {} in MorphingStackWrapper because the given item [ {} ] is not the same", this.stack.getItem(), newStack.getItem());
            return this;
        }
        newStack.remove(MorphToolRegistries.MORPH_DATA);
        newStack.set(DataComponents.ITEM_NAME, this.ogDisplayName); //TODO check if this should be Item Name instead
        return new MorphingStackWrapper(newStack, this.modId, this.ogDisplayName);
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
