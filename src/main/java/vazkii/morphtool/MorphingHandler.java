package vazkii.morphtool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();

	public static final String MINECRAFT = "minecraft";

	public static final String TAG_MORPHING_TOOL = "morphtool:is_morphing";
	public static final String TAG_MORPH_TOOL_DATA = "morphtool:data";
	public static final String TAG_MORPH_TOOL_DISPLAY_NAME = "morphtool:displayName";

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if (!event.getPlayer().isCrouching()) {
			return;
		}

		ItemEntity e = event.getEntity();
		ItemStack stack = e.getItem();
		removeItemFromTool(e, stack, false, e::setItem);
	}

	@SubscribeEvent
	public void onItemBroken(PlayerDestroyItemEvent event) {
		removeItemFromTool(event.getEntity(), event.getOriginal(), true, (ItemStack morph) -> event.getEntity().setItemInHand(event.getHand(), morph));
	}

	public static void removeItemFromTool(Entity e, ItemStack stack, boolean itemBroken, Consumer<ItemStack> consumer) {
		if (stack != null && !stack.isEmpty() && isMorphTool(stack) && stack.getItem() != ModItems.tool) {
			CompoundTag morphData = stack.getTag().getCompound(TAG_MORPH_TOOL_DATA).copy();

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, morphData);
			CompoundTag newMorphData = morph.getTag().getCompound(TAG_MORPH_TOOL_DATA);
			newMorphData.remove(getModFromStack(stack));

			if (!itemBroken) {
				if (!e.getCommandSenderWorld().isClientSide) {
					ItemEntity newItem = new ItemEntity(e.getCommandSenderWorld(), e.getX(), e.getY(), e.getZ(), morph);
					e.getCommandSenderWorld().addFreshEntity(newItem);
				}

				ItemStack copy = stack.copy();
				CompoundTag copyCmp = copy.getTag();
				if (copyCmp == null) {
					copyCmp = new CompoundTag();
					copy.setTag(copyCmp);
				}

				copyCmp.remove("display");
				Component displayName = null;
				CompoundTag nameCmp = (CompoundTag) copyCmp.get(TAG_MORPH_TOOL_DISPLAY_NAME);
				if (nameCmp != null) {
					displayName = Component.literal(nameCmp.getString("text"));
				}
				if (displayName != null && !displayName.getString().isEmpty() && displayName != copy.getHoverName()) {
					copy.setHoverName(displayName);
				}

				copyCmp.remove(TAG_MORPHING_TOOL);
				copyCmp.remove(TAG_MORPH_TOOL_DISPLAY_NAME);
				copyCmp.remove(TAG_MORPH_TOOL_DATA);

				consumer.accept(copy);
			} else {
				consumer.accept(morph);
			}
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(ForgeRegistries.BLOCKS.getKey(state.getBlock()).getNamespace());
	}

	public static String getModFromStack(ItemStack stack) {
		String modId = stack.getItem().getCreatorModId(stack);
		return getModOrAlias(stack.isEmpty() ? MINECRAFT : modId != null ? modId : MINECRAFT);
	}

	public static String getModOrAlias(String mod) {

		Map<String, String> aliases = new HashMap<>();

		for (String s : ConfigHandler.aliasesList.get()) {
			if (s.matches(".+?=.+")) {
				String[] tokens = s.toLowerCase().split("=");
				aliases.put(tokens[0], tokens[1]);
			}
		}

		return aliases.getOrDefault(mod, mod);
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if (!stack.hasTag()) {
			return stack;
		}

		String currentMod = getModFromStack(stack);
		if (mod.equals(currentMod)) {
			return stack;
		}

		CompoundTag morphData = stack.getTag().getCompound(TAG_MORPH_TOOL_DATA);
		return makeMorphedStack(stack, mod, morphData);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, CompoundTag morphData) {
		String currentMod = getModFromStack(currentStack);

		CompoundTag currentCmp = new CompoundTag();
		currentStack.save(currentCmp);
		currentCmp = currentCmp.copy();
		if (currentCmp.contains("tag")) {
			currentCmp.getCompound("tag").remove(TAG_MORPH_TOOL_DATA);
		}

		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(MorphTool.MOD_ID)) {
			morphData.put(currentMod, currentCmp);
		}

		ItemStack stack;
		if (targetMod.equals(MINECRAFT)) {
			stack = new ItemStack(ModItems.tool);
		} else {
			CompoundTag targetCmp = morphData.getCompound(targetMod);
			morphData.remove(targetMod);

			stack = ItemStack.of(targetCmp);
			if (stack.isEmpty()) {
				stack = new ItemStack(ModItems.tool);
			}
		}

		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}

		CompoundTag stackCmp = stack.getTag();
		stackCmp.put(TAG_MORPH_TOOL_DATA, morphData);
		stackCmp.putBoolean(TAG_MORPHING_TOOL, true);

		if (stack.getItem() != ModItems.tool) {
			CompoundTag displayName = new CompoundTag();
			CompoundTag ogDisplayName = displayName;
			displayName.putString("text",  Component.Serializer.toJson(stack.getHoverName()));
			
			if (stackCmp.contains(TAG_MORPH_TOOL_DISPLAY_NAME)) {
				displayName = (CompoundTag) stackCmp.get(TAG_MORPH_TOOL_DISPLAY_NAME);
			} else {
				stackCmp.put(TAG_MORPH_TOOL_DISPLAY_NAME, displayName);
			}

			MutableComponent rawComp = Component.Serializer.fromJson(displayName.getString("text"));
			if(rawComp == null) {
				stackCmp.put(TAG_MORPH_TOOL_DISPLAY_NAME, displayName);
				displayName = ogDisplayName;
			}
			
			Component stackName = rawComp.setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
			Component comp = Component.translatable("morphtool.sudo_name", stackName);
			stack.setHoverName(comp);
		}

		stack.setCount(1);
		return stack;
	}

	private static final Map<String, String> modNames = new HashMap<>();

	static {
		for (IModInfo modEntry : ModList.get().getMods()) {
			modNames.put(modEntry.getModId().toLowerCase(Locale.ENGLISH), modEntry.getDisplayName());
		}
	}

	public static String getModNameForId(String modId) {
		modId = modId.toLowerCase(Locale.ENGLISH);
		return modNames.getOrDefault(modId, modId);
	}

	public static boolean isMorphTool(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		if (stack.getItem() == ModItems.tool) {
			return true;
		}

		return stack.hasTag() && stack.getTag().getBoolean(TAG_MORPHING_TOOL);
	}

	public static HitResult raycast(Entity e, double len) {
		Vec3 vec = new Vec3(e.getX(), e.getY(), e.getZ());
		if (e instanceof Player) {
			vec = vec.add(new Vec3(0, e.getEyeHeight(), 0));
		}

		Vec3 look = e.getLookAngle();
		if (look == null) {
			return null;
		}

		return raycast(e, vec, look, len);
	}

	public static HitResult raycast(Entity e, Vec3 origin, Vec3 ray, double len) {
		Vec3 end = origin.add(ray.normalize().scale(len));
		return e.getCommandSenderWorld().clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
	}
}
