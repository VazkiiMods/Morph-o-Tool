package vazkii.morphtool.network;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import vazkii.arl.network.IMessage;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphingHandler;

@SuppressWarnings("serial")
public class MessageMorphTool implements IMessage {

	public final ItemStack stack;
	public final int slot;

	public MessageMorphTool(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
	}

	@Override
	public boolean receive(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null) {
			context.enqueueWork(() -> {
				ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
				if (MorphingHandler.isMorphTool(mainHandItem) && stack != mainHandItem && !ItemStack.isSame(stack, mainHandItem)) {
					var inventory = player.getInventory();
					inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : slot, stack);
				}
			});
		}
		return true;
	}
}
