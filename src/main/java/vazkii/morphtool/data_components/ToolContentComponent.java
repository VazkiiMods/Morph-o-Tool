package vazkii.morphtool.data_components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToolContentComponent {
    public static final ToolContentComponent EMPTY = new ToolContentComponent(List.of());
    public static final Codec<ToolContentComponent> CODEC = ItemStack.CODEC.listOf().xmap(ToolContentComponent::new, component -> component.items);
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolContentComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ToolContentComponent::new, component -> component.items);
    final List<ItemStack> items;

    public ToolContentComponent(List<ItemStack> contents) {
        this.items = contents;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            return object instanceof ToolContentComponent component && ItemStack.listMatches(this.items, component.items);
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(this.items);
    }

    @Override
    public String toString() {
        return "ToolContents" + this.items;
    }

    public static class Mutable {
        private final List<ItemStack> items;

        public Mutable(ToolContentComponent component) {
            this.items = new ArrayList<>(component.items);
        }

        public void tryInsert(ItemStack stack) {
            if (!stack.isEmpty()) {
                ItemStack itemstack1 = stack.copy();
                this.items.add(itemstack1);
            }
        }

        public void remove(ItemStack stack) {
            this.items.remove(stack);
        }

        public ToolContentComponent toImmutable() {
            return new ToolContentComponent(List.copyOf(this.items));
        }
    }
}
