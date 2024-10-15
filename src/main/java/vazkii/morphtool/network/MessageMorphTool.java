package vazkii.morphtool.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphTool;
import vazkii.morphtool.MorphingHandler;

import java.util.function.Supplier;

public record MessageMorphTool(ItemStack stack, int slot) implements CustomPacketPayload {
	public static final Type<MessageMorphTool> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MorphTool.MOD_ID, "tool_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MessageMorphTool> STREAM_CODEC = CustomPacketPayload.codec(
			MessageMorphTool::serialize,
			MessageMorphTool::new
	);

	public MessageMorphTool(final RegistryFriendlyByteBuf buf) {
		this(ItemStack.STREAM_CODEC.decode(buf), buf.readInt());
	}

	public void serialize(final RegistryFriendlyByteBuf buf) {
		ItemStack.STREAM_CODEC.encode(buf, stack);
		buf.writeInt(slot);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(MessageMorphTool msg, final IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.player() instanceof ServerPlayer player) {
				ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
				if (MorphingHandler.isMorphTool(mainHandItem) && msg.stack() != mainHandItem && !ItemStack.isSameItem(msg.stack(), mainHandItem)) {
					var inventory = player.getInventory();
					inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : msg.slot(), msg.stack());
				}
			}
		}).exceptionally(e -> {
			// Handle exception
			ctx.disconnect(Component.translatable("akashictome.networking.morph_tome.failed", e.getMessage()));
			return null;
		});
	}
}
