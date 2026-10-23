package vazkii.morphtool.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;

import vazkii.morphtool.MorphToolConfig;
import vazkii.morphtool.data_components.MorphingDataComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class MorphHandler {
	public static final MorphHandler INSTANCE = new MorphHandler();
	public static final String MINECRAFT = "minecraft";

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if (!event.getPlayer().isCrouching()) {
			return;
		}

		ItemEntity itemEntity = event.getEntity();
		ItemStack stack = itemEntity.getItem();
		removeItemFromTool(itemEntity, stack, false, itemEntity::setItem);
	}

	@SubscribeEvent
	public void onItemBroken(PlayerDestroyItemEvent event) {
		removeItemFromTool(event.getEntity(), event.getOriginal(), true, morph -> event.getEntity().setItemInHand(event.getHand(), morph));
	}

	public static void removeItemFromTool(Entity relevantEntity, ItemStack stack, boolean itemBroken, Consumer<ItemStack> consumer) {
		if (stack != null && !stack.isEmpty() && isMorphedItem(stack) && !stack.is(MorphToolRegistries.MORPH_TOOL.get())) {
			var data = stack.get(MorphToolRegistries.MORPH_DATA);
			if (data == null)
				return;
			MorphingDataComponent.Mutable mutable = new MorphingDataComponent.Mutable(data);
			var removedStack = mutable.removeCurrent();

			var morphTool = new ItemStack(MorphToolRegistries.MORPH_TOOL.get());
			morphTool.set(MorphToolRegistries.MORPH_DATA, mutable.toImmutable());

			if (itemBroken) {
				consumer.accept(morphTool);
			} else {
				if (!relevantEntity.getCommandSenderWorld().isClientSide()) {
					ItemEntity morphToolEntity = new ItemEntity(relevantEntity.getCommandSenderWorld(), relevantEntity.getX(), relevantEntity.getY(), relevantEntity.getZ(), morphTool);
					relevantEntity.getCommandSenderWorld().addFreshEntity(morphToolEntity);
				}
				consumer.accept(removedStack.copy());
			}
		}
	}

	public static ItemStack createStackOnScroll(MorphingDataComponent data, ItemStack currentStack, int nextIndex) {
		var mutable = new MorphingDataComponent.Mutable(data);
		var nextStack = mutable.turnToNextItem(currentStack, nextIndex);
		if (finalizeItemMorph(mutable, nextStack))
			return nextStack;
		return ItemStack.EMPTY;
	}

	public static ItemStack createStackOnLookAt(ItemStack currentStack, String modLookedAt) {
		if (modLookedAt.equals(MINECRAFT) && currentStack.is(MorphToolRegistries.MORPH_TOOL))
			return currentStack;

		var data = currentStack.get(MorphToolRegistries.MORPH_DATA);
		if (data == null)
			return currentStack;

		var mutable = new MorphingDataComponent.Mutable(data);

		var nextStack = mutable.turnToFirstItemOfMod(currentStack, modLookedAt);

		if (finalizeItemMorph(mutable, nextStack))
			return nextStack;

		return currentStack;
	}

	/**
	 * Validates the item, sets the remaining data to the new item, calculates the new name and sets the count.
	 *
	 * @param mutable   the remaining morph data
	 * @param nextStack the item the morph tool turn into
	 * @return true if finalization completed successfully
	 */
	private static boolean finalizeItemMorph(MorphingDataComponent.Mutable mutable, ItemStack nextStack) {
		if (!nextStack.isEmpty()) {
			nextStack.set(MorphToolRegistries.MORPH_DATA, mutable.toImmutable());

			if (!nextStack.is(MorphToolRegistries.MORPH_TOOL.get())) {
				Component hoverName = nextStack.getHoverName();
				Component stackName = Component.literal(hoverName.getString()).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
				Component comp = Component.translatable("morphtool.sudo_name", stackName);
				nextStack.set(DataComponents.CUSTOM_NAME, comp);
			}

			nextStack.setCount(1);
			return true;
		}
		return false;
	}

	/**
	 * Checks if the given item is an untransformed Morph Tool or an item that the Morph Tool has turned into.
	 */
	public static boolean isMorphToolOrMorphed(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		if (stack.is(MorphToolRegistries.MORPH_TOOL.get())) {
			return true;
		}

		return isMorphedItem(stack);
	}

	public static boolean isMorphedItem(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return stack.has(MorphToolRegistries.MORPH_DATA); //Only Morph Tool or Items inside the Morph Tool should have this data.
	}

	public static ItemStack getItemFromCorrectMainHand(Player player) {
		return player.getItemInHand(MorphToolConfig.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

	public static String getModFromState(BlockState state) {
		var modOfBlock = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace();
		return getModOrAlias(modOfBlock);
	}

	public static String getDirectModFromStack(ItemStack stack) {
		return stack.getItem().getCreatorModId(stack);
	}

	//Maps modId to alias modId. Will change in the future
	public static String getModOrAlias(String mod) {
		Map<String, String> aliases = new HashMap<>();

		for (String s : MorphToolConfig.aliasesList.get()) {
			if (s.matches(".+?=.+")) {
				String[] tokens = s.toLowerCase().split("=");
				aliases.put(tokens[0], tokens[1]);
			}
		}

		return aliases.getOrDefault(mod, mod);
	}
}
