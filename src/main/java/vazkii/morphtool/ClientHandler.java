package vazkii.morphtool;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.morphtool.network.MessageMorphTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientHandler {

    public static final ClientHandler INSTANCE = new ClientHandler();
    protected static boolean autoMode = true;

    //Priority Highest so that it happens before the other mods
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(MouseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack mainHandItem = player.getHeldItem(ConfigHandler.invertHandShift ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if(MorphingHandler.isMorphTool(mainHandItem)) {
            ItemStack newStack = mainHandItem;
            RayTraceResult res = MorphingHandler.raycast(player, 4.5);
            String mod = MorphingHandler.getModFromStack(mainHandItem);
            String modlook = "";

            //Get looked at Mod
            if (res != null) {
                IBlockState state = player.world.getBlockState(res.getBlockPos());
                modlook = MorphingHandler.getModFromState(state);
                //Morph tool to looked at Mod
                if(autoMode && event.getDwheel() == 0){
                    newStack = MorphingHandler.getShiftStackForMod(mainHandItem, modlook);
                }
            }
            //Manual Scroll for Morph (exluding looked at a mod block incase it also needs scrolling)
            if (event.getDwheel() != 0 && player.isSneaking() && !modlook.equals(mod)) {
                if(mainHandItem.getTagCompound() != null){
                    NBTTagCompound morphData = mainHandItem.getTagCompound().getCompoundTag(MorphingHandler.TAG_MORPH_TOOL_DATA);
                    mod = event.getDwheel() < 0 ? nextMod(morphData, mod) : previousMod(morphData, mod);
                    newStack = MorphingHandler.getShiftStackForMod(mainHandItem, mod);
                    autoMode = mod.equals("morphtool");
                    event.setCanceled(true);
                }
            }

            if(newStack != mainHandItem && !ItemStack.areItemsEqual(newStack, mainHandItem)) {
                player.inventory.setInventorySlotContents(ConfigHandler.invertHandShift ? player.inventory.getSizeInventory() - 1 : player.inventory.currentItem, newStack);
                NetworkHandler.INSTANCE.sendToServer(new MessageMorphTool(newStack, player.inventory.currentItem));
                MorphTool.proxy.updateEquippedItem();
            }
        }
    }

    public static String nextMod(NBTTagCompound morphData, String mod) {
        List<String> mods = new ArrayList<>(morphData.getKeySet());
        mods.add("morphtool");
        if (!mod.equals("morphtool")){
            mods.add(mod);
        }
        Collections.sort(mods);
        int id = mods.indexOf(mod);
        int retid = 0;
        //Move up the Array
        if(mods.size() > (id + 1)){
            retid = id + 1;
        }
        return mods.get(retid);
    }

    public static String previousMod(NBTTagCompound morphData, String mod) {
        List<String> mods = new ArrayList<>(morphData.getKeySet());
        mods.add("morphtool");
        if (!mod.equals("morphtool")){
            mods.add(mod);
        }
        Collections.sort(mods);
        int id = mods.indexOf(mod);
        int retid = mods.size() - 1;
        //Move down the Array
        if(0 <= (id - 1)){
            retid = (id - 1);
        }
        return mods.get(retid);
    }

}
