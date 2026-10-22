package vazkii.morphtool.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import vazkii.morphtool.MorphToolConfig;
import vazkii.morphtool.common.MorphHandler;
import vazkii.morphtool.common.MorphToolRegistries;
import vazkii.morphtool.network.MessageMorphTool;
import vazkii.morphtool.network.NetworkHandler;

public class MorphHandlerClient {
	public static final MorphHandlerClient INSTANCE = new MorphHandlerClient();
	protected static boolean autoMode = true;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player != null && autoMode) {
			ItemStack mainHandItem = MorphHandler.getItemFromCorrectMainHand(player);
			if (MorphHandler.isMorphToolOrMorphed(mainHandItem)) {
				ItemStack newStack = mainHandItem;
				HitResult res = MorphHandlerClient.raycast(player, 4.5);

				//Get looked at Mod
				if (res.getType() == HitResult.Type.BLOCK && res instanceof BlockHitResult blockHitResult) {
					var modLook = getModFromLookedAt(player, blockHitResult);
					newStack = MorphHandler.createStackOnLookAt(mainHandItem, modLook);
				}

				updateItemInInventory(player, mainHandItem, newStack);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMouseEvent(InputEvent.MouseScrollingEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			ItemStack mainHandItem = MorphHandler.getItemFromCorrectMainHand(player);
			if (MorphHandler.isMorphToolOrMorphed(mainHandItem)) {
				ItemStack newStack = mainHandItem;
				String modOfCurrentStack = MorphHandler.getDirectModFromStack(mainHandItem);
				HitResult res = raycast(player, 4.5);
				String modLook = "";

				//Get looked at Mod
				if (res.getType() == HitResult.Type.BLOCK && res instanceof BlockHitResult blockHitResult) {
					modLook = getModFromLookedAt(player, blockHitResult);
				}

				//Manual Scroll for Morph (excluding looked at a mod block incase it also needs scrolling)
				if (event.getScrollDeltaY() != 0 && player.isCrouching() && !modLook.equals(MorphHandler.getModOrAlias(modOfCurrentStack))) {
					if (mainHandItem.has(MorphToolRegistries.MORPH_DATA)) {
						var data = mainHandItem.get(MorphToolRegistries.MORPH_DATA);
						if (data == null)
							return;
						if (data.isEmpty())
							return;
						int currentIndex = data.getCurrentItemIndex();
						int nextIndex = getNextIndex(currentIndex, data.size(), event.getScrollDeltaY());
						autoMode = nextIndex == -1;
						newStack = MorphHandler.createStackOnScroll(data, mainHandItem, nextIndex);
						event.setCanceled(true);
					}
				}
				updateItemInInventory(player, mainHandItem, newStack);
			}
		}
	}

	/**
	 * Checks if the item is different and then sets the new item in the slot and sends a packet to the server.
	 *
	 * @param player       the current player
	 * @param mainHandItem the previous item in the slot
	 * @param newStack     the item to be set in the slot
	 */
	private void updateItemInInventory(Player player, ItemStack mainHandItem, ItemStack newStack) {
		if (newStack != mainHandItem && !ItemStack.isSameItemSameComponents(newStack, mainHandItem)) {
			var inventory = player.getInventory();
			inventory.setItem(MorphToolConfig.invertHandShift.get() ? inventory.getContainerSize() - 1 : inventory.selected, newStack);
			NetworkHandler.sendToServer(new MessageMorphTool(newStack, inventory.selected));
			updateEquippedItem();
		}
	}

	private static String getModFromLookedAt(Player player, BlockHitResult res) {
		BlockState state = player.level().getBlockState(res.getBlockPos());
		return MorphHandler.getModFromState(state);
	}

	private static int getNextIndex(int currentIndex, int totalSize, double scrollDelta) {
		int newIndex = currentIndex;
		int maxIndex = totalSize - 1;
		if (scrollDelta > 0) { //UP
			newIndex++;
			if (newIndex > maxIndex) {
				newIndex = -1;
			}
		} else { // DOWN
			if (newIndex == -1) {
				newIndex = maxIndex; //Set to last entry (maxIndex) when morphtool (-1)
			} else {
				newIndex--;
			}
		}
		return newIndex;
	}

	private void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(MorphToolConfig.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

	private static HitResult raycast(Entity e, double len) {
		Vec3 vec = new Vec3(e.getX(), e.getY(), e.getZ());
		if (e instanceof Player) {
			vec = vec.add(new Vec3(0, e.getEyeHeight(), 0));
		}

		return raycast(e, vec, e.getLookAngle(), len);
	}

	private static HitResult raycast(Entity e, Vec3 origin, Vec3 ray, double len) {
		Vec3 end = origin.add(ray.normalize().scale(len));
		return e.getCommandSenderWorld().clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
	}
}
