package vazkii.morphtool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import vazkii.morphtool.data_components.ToolContentComponent;

import java.util.*;
import java.util.function.Consumer;

public final class MorphingHandler {
	public static final MorphingHandler INSTANCE = new MorphingHandler();

	public static final String MINECRAFT = "minecraft";

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

	//TODO next fix this
	public static void removeItemFromTool(Entity e, ItemStack stack, boolean itemBroken, Consumer<ItemStack> consumer) {
		if (stack != null && !stack.isEmpty() && isMorphTool(stack) && !stack.is(Registries.MORPH_TOOL.get())) {
			ToolContentComponent contents = stack.get(Registries.TOOL_CONTENT);
			//CompoundTag morphData = stack.getTag().getCompound(TAG_MORPH_TOOL_DATA).copy();

			ItemStack morph = makeMorphedStack(stack, MINECRAFT);
			String mod = getModFromStack(stack);
			List<ItemStack> newStacks = new ArrayList<>(List.copyOf(contents.getItems()));
			newStacks.remove(getStackFromMod(contents, mod));
			ToolContentComponent newContents = new ToolContentComponent(newStacks);

			/*
			CompoundTag newMorphData = morph.getTag().getCompound(TAG_MORPH_TOOL_DATA);
			newMorphData.remove(getModFromStack(stack));

			 */

			if (!itemBroken) {
				if (!e.getCommandSenderWorld().isClientSide) {
					ItemEntity newItem = new ItemEntity(e.getCommandSenderWorld(), e.getX(), e.getY(), e.getZ(), morph);
					e.getCommandSenderWorld().addFreshEntity(newItem);
				}

				ItemStack copy = stack.copy();
				copy.set(Registries.TOOL_CONTENT, newContents);
				/*
				CompoundTag copyCmp = copy.getTag();
				if (copyCmp == null) {
					copyCmp = new CompoundTag();
					copy.setTag(copyCmp);
				}

				copyCmp.remove("display");

				copyCmp.remove(TAG_MORPHING_TOOL);
				copyCmp.remove(TAG_MORPH_TOOL_DISPLAY_NAME);
				copyCmp.remove(TAG_MORPH_TOOL_DATA);

				 */

				consumer.accept(copy);
			} else {
				consumer.accept(morph);
			}
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
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
		if (!stack.has(Registries.TOOL_CONTENT)) {
			return stack;
		}

		String currentMod = getModFromStack(stack);
		if (mod.equals(currentMod)) {
			return stack;
		}

		return makeMorphedStack(stack, mod);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod) {
		String currentMod = getModFromStack(currentStack);
		ToolContentComponent currentContent = currentStack.get(Registries.TOOL_CONTENT);
		ToolContentComponent newStackComponent = new ToolContentComponent(List.of(currentStack));
		if (currentContent == null) return ItemStack.EMPTY;

		ToolContentComponent.Mutable mutable = getMutable(currentContent, newStackComponent, currentMod);

		ItemStack stack;
		if (targetMod.equals(MINECRAFT)) {
			stack = new ItemStack(Registries.MORPH_TOOL.get());
		} else {
			stack = getStackFromMod(currentContent, targetMod);

			if (stack.isEmpty()) {
				stack = new ItemStack(Registries.MORPH_TOOL.get());
			}
		}

		/*
		if (!stack.hasTag()) {
			stack.setTag(new CompoundTag());
		}
		 */

		mutable.remove(stack);

		stack.set(Registries.TOOL_CONTENT, mutable.toImmutable());
		stack.set(Registries.IS_MORPH_TOOL, true);

		/*
		CompoundTag stackCmp = stack.getTag();
		stackCmp.put(TAG_MORPH_TOOL_DATA, morphData);
		stackCmp.putBoolean(TAG_MORPHING_TOOL, true);

		 */

		if (!stack.is(Registries.MORPH_TOOL.get())) {

			/*
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

			 */


			Component hoverName = stack.getHoverName();

			if (!stack.has(Registries.OG_DISPLAY_NAME)) {
				stack.set(Registries.OG_DISPLAY_NAME, hoverName);
			} else {
				hoverName = stack.get(Registries.OG_DISPLAY_NAME);
			}
			
			Component stackName = Component.literal(hoverName.getString()).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
			Component comp = Component.translatable("morphtool.sudo_name", stackName);
			stack.set(DataComponents.CUSTOM_NAME, comp);

		}

		stack.setCount(1);
		return stack;
	}

	private static ToolContentComponent.Mutable getMutable(ToolContentComponent currentContent, ToolContentComponent newStackComponent, String currentMod) {
		ToolContentComponent.Mutable currentContentMutable = new ToolContentComponent.Mutable(currentContent);
		ToolContentComponent.Mutable newStackComponentMutable = new ToolContentComponent.Mutable(newStackComponent);

		/*
		CompoundTag currentCmp = new CompoundTag();
		currentStack.save(currentCmp);
		currentCmp = currentCmp.copy();
		if (currentCmp.contains("tag")) {
			currentCmp.getCompound("tag").remove(TAG_MORPH_TOOL_DATA);
		}

		 */

		if (!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(MorphTool.MOD_ID)) {
			currentContentMutable.tryInsert(newStackComponent.getItems().getFirst());
		}
		return currentContentMutable;
	}

	public static ItemStack getStackFromMod(ToolContentComponent component, String mod) {
		if (component != null && !component.isEmpty()) {
			for (ItemStack contentStack : component.getItems()) {
				if (BuiltInRegistries.ITEM.getKey(contentStack.getItem()).getNamespace().equals(mod)) {
					return contentStack;
				}
			}
		}
		return ItemStack.EMPTY;
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

		if (stack.is(Registries.MORPH_TOOL.get())) {
			return true;
		}

		return stack.has(Registries.IS_MORPH_TOOL) && Boolean.TRUE.equals(stack.get(Registries.IS_MORPH_TOOL));
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
