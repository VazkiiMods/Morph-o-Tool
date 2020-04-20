package vazkii.morphtool;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.morphtool.network.MessageMorphTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {

    public static final ClientHandler INSTANCE = new ClientHandler();
    protected static boolean autoMode = true;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(ClientTickEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null && event.phase == Phase.END && autoMode) {
            ItemStack mainHandItem = player.getHeldItem(ConfigHandler.invertHandShift.get() ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (MorphingHandler.isMorphTool(mainHandItem)) {
            	System.out.println("do be morphin tho");
                ItemStack newStack = mainHandItem;
                RayTraceResult res = MorphingHandler.raycast(player, 4.5);

                //Get looked at Mod
                if (res != null && res.getType() == RayTraceResult.Type.BLOCK) {
                    BlockState state = player.world.getBlockState(((BlockRayTraceResult) res).getPos());
                    String modlook = MorphingHandler.getModFromState(state);
                    //Morph tool to looked at Mod
                    newStack = MorphingHandler.getShiftStackForMod(mainHandItem, modlook);
                }

                if (newStack != mainHandItem && !ItemStack.areItemsEqual(newStack, mainHandItem)) {
                    player.inventory.setInventorySlotContents(ConfigHandler.invertHandShift.get() ? player.inventory.getSizeInventory() - 1 : player.inventory.currentItem, newStack);
                    MorphTool.NETWORKHANDLER.sendToServer(new MessageMorphTool(newStack, player.inventory.currentItem));
                    MorphTool.proxy.updateEquippedItem();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseEvent(InputEvent.MouseScrollEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player != null) {
            ItemStack mainHandItem = player.getHeldItem(ConfigHandler.invertHandShift.get() ? Hand.OFF_HAND : Hand.MAIN_HAND);
            if (MorphingHandler.isMorphTool(mainHandItem)) {
                ItemStack newStack = mainHandItem;
                String mod = MorphingHandler.getModFromStack(mainHandItem);
                RayTraceResult res = MorphingHandler.raycast(player, 4.5);
                String modlook = "";

                //Get looked at Mod
                if (res != null && res.getType() == RayTraceResult.Type.BLOCK) {
                    BlockState state = player.world.getBlockState(((BlockRayTraceResult) res).getPos());
                    modlook = MorphingHandler.getModFromState(state);
                }

                //Manual Scroll for Morph (excluding looked at a mod block incase it also needs scrolling)
                if (event.getScrollDelta() != 0 && player.isCrouching() && !modlook.equals(mod)) {
                    if (mainHandItem.getTag() != null) {
                        CompoundNBT morphData = mainHandItem.getTag().getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
                        mod = event.getScrollDelta() < 0 ? nextMod(morphData, mod) : previousMod(morphData, mod);
                        newStack = MorphingHandler.getShiftStackForMod(mainHandItem, mod);
                        autoMode = mod.equals("morphtool");
                        event.setCanceled(true);
                    }
                }

                if (newStack != mainHandItem && !ItemStack.areItemsEqual(newStack, mainHandItem)) {
                    player.inventory.setInventorySlotContents(ConfigHandler.invertHandShift.get() ? player.inventory.getSizeInventory() - 1 : player.inventory.currentItem, newStack);
                    MorphTool.NETWORKHANDLER.sendToServer(new MessageMorphTool(newStack, player.inventory.currentItem));
                    MorphTool.proxy.updateEquippedItem();
                }
            }
        }
    }


    public static String nextMod(CompoundNBT morphData, String mod) {
        List<String> mods = new ArrayList<>(morphData.keySet());
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

    public static String previousMod(CompoundNBT morphData, String mod) {
        List<String> mods = new ArrayList<>(morphData.keySet());
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
