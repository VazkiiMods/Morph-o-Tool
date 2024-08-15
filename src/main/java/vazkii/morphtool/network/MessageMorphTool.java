package vazkii.morphtool.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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
	public static final CustomPacketPayload.Type<MessageMorphTool> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MorphTool.MOD_ID, "tool_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MessageMorphTool> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC,
			MessageMorphTool::stack,
			ByteBufCodecs.VAR_INT,
			MessageMorphTool::slot,
			MessageMorphTool::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static class Handler implements IPayloadHandler<MessageMorphTool> {

		public void handle(final MessageMorphTool msg, final IPayloadContext context) {
			Player player = context.player();
			ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
			if (MorphingHandler.isMorphTool(mainHandItem) && msg.stack() != mainHandItem && !ItemStack.isSameItem(msg.stack(), mainHandItem)) {
				var inventory = player.getInventory();
				inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : msg.slot(), msg.stack());
			}
		}
	}
}
