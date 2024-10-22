package vazkii.morphtool;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import vazkii.morphtool.data_components.ToolContentComponent;
import vazkii.morphtool.network.MessageMorphTool;
import vazkii.morphtool.network.NetworkHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {
	public static final ClientHandler INSTANCE = new ClientHandler();
	protected static boolean autoMode = true;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player != null && autoMode) {
			ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
			if (MorphingHandler.isMorphTool(mainHandItem)) {
				ItemStack newStack = mainHandItem;
				HitResult res = MorphingHandler.raycast(player, 4.5);

				//Get looked at Mod
				if (res != null && res.getType() == HitResult.Type.BLOCK) {
					BlockState state = player.level().getBlockState(((BlockHitResult) res).getBlockPos());
					String modlook = MorphingHandler.getModFromState(state);
					//Morph tool to looked at Mod
					newStack = MorphingHandler.getShiftStackForMod(mainHandItem, modlook);
				}

				if (newStack != mainHandItem && !ItemStack.isSameItemSameComponents(newStack, mainHandItem)) {
					var inventory = player.getInventory();
					inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : inventory.selected, newStack);
					NetworkHandler.sendToServer(new MessageMorphTool(newStack, inventory.selected));
					MorphTool.proxy.updateEquippedItem();
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ItemStack mainHandItem = player.getItemInHand(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
			if (MorphingHandler.isMorphTool(mainHandItem)) {
				ItemStack newStack = mainHandItem;
				String mod = MorphingHandler.getModFromStack(mainHandItem);
				HitResult res = MorphingHandler.raycast(player, 4.5);
				String modlook = "";

				//Get looked at Mod
				if (res != null && res.getType() == HitResult.Type.BLOCK) {
					BlockState state = player.level().getBlockState(((BlockHitResult) res).getBlockPos());
					modlook = MorphingHandler.getModFromState(state);
				}

				//Manual Scroll for Morph (excluding looked at a mod block incase it also needs scrolling)
				if (event.getScrollDeltaY() != 0 && player.isCrouching() && !modlook.equals(mod)) {
					if (mainHandItem.has(Registries.TOOL_CONTENT) && mainHandItem.get(Registries.TOOL_CONTENT) != null) {
						ToolContentComponent contents = mainHandItem.get(Registries.TOOL_CONTENT);
						mod = event.getScrollDeltaY() < 0 ? nextMod(contents, mod) : previousMod(contents, mod);
						newStack = MorphingHandler.getShiftStackForMod(mainHandItem, mod);
						autoMode = mod.equals("morphtool");
						event.setCanceled(true);
					}
				}
				if (newStack != mainHandItem && !ItemStack.isSameItemSameComponents(newStack, mainHandItem)) {
					var inventory = player.getInventory();
					inventory.setItem(ConfigHandler.invertHandShift.get() ? inventory.getContainerSize() - 1 : inventory.selected, newStack);
					NetworkHandler.sendToServer(new MessageMorphTool(newStack, inventory.selected));
					MorphTool.proxy.updateEquippedItem();
				}
			}
		}
	}

	public static List<String> getModsFromStacks(ToolContentComponent toolContents) {
		List<String> mods = new ArrayList<>();
		for (ItemStack stack : toolContents.getItems()) {
			mods.add(MorphingHandler.getModFromStack(stack));
		}
		return mods;
	}

	public static String nextMod(ToolContentComponent toolContents, String mod) {
		List<String> mods = getModsFromStacks(toolContents);
		mods.add("morphtool");
		if (!mod.equals("morphtool")) {
			mods.add(mod);
		}
		Collections.sort(mods);
		int id = mods.indexOf(mod);
		int retid = 0;
		//Move up the Array
		if (mods.size() > (id + 1)) {
			retid = id + 1;
		}
		return mods.get(retid);
	}

	public static String previousMod(ToolContentComponent toolContents, String mod) {
		List<String> mods = getModsFromStacks(toolContents);
		mods.add("morphtool");
		if (!mod.equals("morphtool")) {
			mods.add(mod);
		}
		Collections.sort(mods);
		int id = mods.indexOf(mod);
		int retid = mods.size() - 1;
		//Move down the Array
		if (0 <= (id - 1)) {
			retid = (id - 1);
		}
		return mods.get(retid);
	}
}