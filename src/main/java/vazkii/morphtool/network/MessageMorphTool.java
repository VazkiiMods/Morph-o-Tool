package vazkii.morphtool.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphingHandler;

import java.util.function.Supplier;

public class MessageMorphTool {
	public ItemStack stack;
	public int slot;

	public MessageMorphTool() {}

	public MessageMorphTool(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
	}

	public static void serialize(final MessageMorphTool msg, final FriendlyByteBuf buf) {
		buf.writeItem(msg.stack);
		buf.writeVarInt(msg.slot);
	}

	public static MessageMorphTool deserialize(final FriendlyByteBuf buf) {
		final MessageMorphTool msg = new MessageMorphTool();
		msg.stack = buf.readItem();
		msg.slot = buf.readVarInt();
		return msg;
	}

	public static void handle(MessageMorphTool msg, Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();
		Player player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
				if (MorphingHandler.isMorphTool(mainHandItem) && msg.stack != mainHandItem && !ItemStack.isSameItem(msg.stack, mainHandItem)) {
					var inventory = player.getInventory();
					inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : msg.slot, msg.stack);
				}
			});
		}
		context.setPacketHandled(true);
	}
}
