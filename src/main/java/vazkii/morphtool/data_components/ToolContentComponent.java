package vazkii.morphtool.data_components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ToolContentComponent(List<ItemStack> contents) {
    public static final ToolContentComponent EMPTY = new ToolContentComponent(List.of());
    public static final Codec<ToolContentComponent> CODEC = ItemStack.CODEC.listOf().xmap(ToolContentComponent::new, component -> component.contents);
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolContentComponent> STREAM_CODEC = ItemStack.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ToolContentComponent::new, component -> component.contents);

    public boolean isEmpty() {
        return this.contents.isEmpty();
    }
}
