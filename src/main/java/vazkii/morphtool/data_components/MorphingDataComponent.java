package vazkii.morphtool.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import vazkii.morphtool.common.MorphToolRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MorphingDataComponent {
    public static final Codec<MorphingDataComponent> CODEC = RecordCodecBuilder.create(obj -> obj.group(
            Codec.BOOL.fieldOf("is_morphed").forGetter(i -> i.isMorphed),
            MorphingStackWrapper.CODEC.listOf().fieldOf("morph_items").forGetter(i -> i.morphingItemList),
            Codec.INT.fieldOf("current_item_index").forGetter(i -> i.currentItemIndex),
            NeoForgeExtraCodecs.setOf(Codec.STRING).fieldOf("available_mod_ids").forGetter(i -> i.availableModIds)
    ).apply(obj, MorphingDataComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MorphingDataComponent> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public static final MorphingDataComponent DEFAULT = new MorphingDataComponent(false, List.of(), -1, Set.of());

    private final boolean isMorphed;
    private final List<MorphingStackWrapper> morphingItemList;
    private final int currentItemIndex; // -1 means Morph Tool
    private final Set<String> availableModIds;

    MorphingDataComponent(boolean isMorphed, List<MorphingStackWrapper> morphingItemList, int currentItemIndex, Set<String> availableModIds) {
        this.isMorphed = isMorphed;
        this.morphingItemList = morphingItemList;
        this.currentItemIndex = currentItemIndex;
        this.availableModIds = availableModIds;
    }

    public boolean isMorphedItem() {
        return isMorphed;
    }

    public boolean isMorphTool() {
        return !isMorphed;
    }

    public Iterable<MorphingStackWrapper> getMorphingWrappers() {
        return morphingItemList;
    }

    public boolean isEmpty() {
        return this.morphingItemList.isEmpty();
    }

    public int size() {
        return morphingItemList.size();
    }

    public Set<String> getAvailableModIds() {
        return availableModIds;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof MorphingDataComponent data) {
            return this.isMorphed == data.isMorphed
                    && MorphingStackWrapper.listMatches(this.morphingItemList, data.morphingItemList)
                    && this.currentItemIndex == data.currentItemIndex
                    && this.availableModIds.equals(data.availableModIds);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int i = 0;
        for (MorphingStackWrapper wrapper : this.getMorphingWrappers()) {
            i = i * 31 + ItemStack.hashItemAndComponents(wrapper.getStack());
        }
        return i;
    }

    @Override
    public String toString() {
        return "MorphToolData" + this.morphingItemList;
    }

    public static class Mutable {
        private boolean isMorphed;
        private final List<MorphingStackWrapper> morphingItemList;
        private int currentItemIndex;
        private final Set<String> availableModIds;

        public Mutable(MorphingDataComponent morphData) {
            this.isMorphed = morphData.isMorphed;
            this.morphingItemList = new ArrayList<>(morphData.morphingItemList);
            this.currentItemIndex = morphData.currentItemIndex;
            this.availableModIds = new HashSet<>(morphData.availableModIds);
        }

        public void setMorphed() {
            isMorphed = true;
        }

        public void setNotMorphed() {
            isMorphed = false;
        }

        /**
         * Adds the given modId to the set if it doesn't exist there yet.
         */
        public void addModId(String modId) {
            availableModIds.add(modId);
        }

        /**
         * Adds a new item to the morphing data. This is used by the crafting recipe.
         * @param stack the stack to add
         */
        public boolean tryAddNewItem(ItemStack stack) {
            if (stack.isEmpty()) return false;
            var stackCopy = stack.copy();
            var modId = BuiltInRegistries.ITEM.getKey(stackCopy.getItem()).getNamespace();
            var currentName = stack.getHoverName();
            var stackWrapper = new MorphingStackWrapper(stackCopy, modId, currentName);
            this.morphingItemList.add(stackWrapper);
            this.addModId(modId);
            return true;
        }

        /**
         * Checks if the index is valid, sets the data to morphed and sets the new current index and then returns the stack to morph into.
         */
        public ItemStack turnToNextItem(ItemStack currentStack, int nextIndex) {
            if (this.morphingItemList.isEmpty() || nextIndex > this.morphingItemList.size()) return ItemStack.EMPTY;
            if (currentStack.isEmpty()) return ItemStack.EMPTY;

            // Update should only be called if it currently isn't Morph Tool
            if (this.currentItemIndex != -1) {
                updateWrapperForCurrentIndex(currentStack);
            }

            ItemStack stack;

            if (nextIndex == -1) { //If it should turn back to morph tool
                stack = new ItemStack(MorphToolRegistries.MORPH_TOOL.get());
                setNotMorphed();
            } else {
                // Calculate next wrapper, set morphed and change index
                var nextWrapper = this.morphingItemList.get(nextIndex);
                stack = nextWrapper.getStack().copy();
                setMorphed();
            }

            this.currentItemIndex = nextIndex;

            // Return the new stack
            return stack;
        }

        /**
         * Retrieves the wrapper of the current index and updates the stack by providing the updated stack. <br>
         * This should be called when switching from one morphed item to another.
         */
        private void updateWrapperForCurrentIndex(ItemStack updatedStack) {
            var currentWrapper = this.morphingItemList.get(this.currentItemIndex);
            this.morphingItemList.set(this.currentItemIndex, currentWrapper.updateStack(updatedStack.copy()));
        }

        public MorphingDataComponent toImmutable() {
            return new MorphingDataComponent(this.isMorphed, List.copyOf(this.morphingItemList), this.currentItemIndex, Set.copyOf(this.availableModIds));
        }
    }

}
