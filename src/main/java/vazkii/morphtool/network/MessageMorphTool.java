package vazkii.morphtool.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphingHandler;

public class MessageMorphTool extends NetworkMessage{

    public ItemStack stack;
    public int slot;

    public MessageMorphTool() {}

    public MessageMorphTool(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public IMessage handleMessage(MessageContext context) {
        EntityPlayer player = context.getServerHandler().playerEntity;
        ItemStack mainHandItem = player.getHeldItem(ConfigHandler.invertHandShift ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if(MorphingHandler.isMorphTool(mainHandItem) && stack != mainHandItem && !ItemStack.areItemsEqual(stack, mainHandItem) ) {
            player.inventory.setInventorySlotContents(ConfigHandler.invertHandShift ? player.inventory.getSizeInventory() - 1 : slot, stack);
        }
        return null;
    }


}
