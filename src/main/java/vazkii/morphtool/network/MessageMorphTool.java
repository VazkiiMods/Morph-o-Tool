package vazkii.morphtool.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.arl.network.IMessage;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphingHandler;

public class MessageMorphTool implements IMessage {

	private static final long serialVersionUID = 8883750897743016439L;
	
	public ItemStack stack;
    public int slot;

    public MessageMorphTool() {}

    public MessageMorphTool(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }


    @Override
    public boolean receive(NetworkEvent.Context context) {
        PlayerEntity player = context.getSender();
        if(player != null) {
        	context.enqueueWork(() -> {
        		ItemStack mainHandItem = player.getHeldItem(ConfigHandler.invertHandShift.get() ? Hand.OFF_HAND : Hand.MAIN_HAND);
                if (MorphingHandler.isMorphTool(mainHandItem) && stack != mainHandItem && !ItemStack.areItemsEqual(stack, mainHandItem)) {
                    player.inventory.setInventorySlotContents(ConfigHandler.invertHandShift.get() ? player.inventory.getSizeInventory() - 1 : slot, stack);
                }
        	});
        }
        return true;
    }
}
