package vazkii.morphtool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();

	public static final String MINECRAFT = "minecraft";

	public static final String TAG_MORPHING_TOOL = "morphtool:is_morphing";
	public static final String TAG_MORPH_TOOL_DATA = "morphtool:data";
	public static final String TAG_MORPH_TOOL_DISPLAY_NAME = "morphtool:displayName";

	@SubscribeEvent
	public void onItemDropped(ItemTossEvent event) {
		if(!event.getPlayer().isCrouching())
			return;

		ItemEntity e = event.getEntityItem();
		ItemStack stack = e.getItem();
		removeItemFromTool(e, stack, false, e::setItem);
	}

	@SubscribeEvent
	public void onItemBroken(PlayerDestroyItemEvent event) {
		removeItemFromTool(event.getPlayer(), event.getOriginal(), true, (ItemStack morph) -> event.getPlayer().setHeldItem(event.getHand(), morph));
	}

	public static void removeItemFromTool(Entity e, ItemStack stack, boolean itemBroken, Consumer<ItemStack> consumer) {
		if(stack != null && !stack.isEmpty() && isMorphTool(stack) && stack.getItem() != ModItems.tool) {
			CompoundNBT morphData = stack.getTag().getCompound(TAG_MORPH_TOOL_DATA).copy();

			ItemStack morph = makeMorphedStack(stack, MINECRAFT, morphData);
			CompoundNBT newMorphData = morph.getTag().getCompound(TAG_MORPH_TOOL_DATA);
			newMorphData.remove(getModFromStack(stack));

			if(!itemBroken) {
				if(!e.getEntityWorld().isRemote) {
					ItemEntity newItem = new ItemEntity(e.getEntityWorld(), e.serverPosX, e.serverPosY, e.serverPosZ, morph);
					e.getEntityWorld().addEntity(newItem);
				}

				ItemStack copy = stack.copy();
				CompoundNBT copyCmp = copy.getTag();
				if(copyCmp == null) {
					copyCmp = new CompoundNBT();
					copy.setTag(copyCmp);
				}

				copyCmp.remove("display");
				String displayName = copyCmp.getString(TAG_MORPH_TOOL_DISPLAY_NAME);
				if(!displayName.isEmpty() && !displayName.equals(copy.getDisplayName().getString()))
					copy.setDisplayName(ITextComponent.Serializer.func_240643_a_(displayName));

				copyCmp.remove(TAG_MORPHING_TOOL);
				copyCmp.remove(TAG_MORPH_TOOL_DISPLAY_NAME);
				copyCmp.remove(TAG_MORPH_TOOL_DATA);

				consumer.accept(copy);
			} else consumer.accept(morph);
		}
	}

	public static String getModFromState(BlockState state) {
		return getModOrAlias(state.getBlock().getRegistryName().getNamespace());
	}

	public static String getModFromStack(ItemStack stack) {
		String modId = stack.getItem().getCreatorModId(stack);
		return getModOrAlias(stack.isEmpty() ? MINECRAFT : modId != null ? modId : MINECRAFT);
	}

	public static String getModOrAlias(String mod) {

		Map<String, String> aliases = new HashMap<>();

		for(String s : ConfigHandler.aliasesList.get())
			if(s.matches(".+?=.+")) {
				String[] tokens = s.toLowerCase().split("=");
				aliases.put(tokens[0], tokens[1]);
			}

		return aliases.getOrDefault(mod, mod);
	}

	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if(!stack.hasTag())
			return stack;

		String currentMod = getModFromStack(stack);
		if(mod.equals(currentMod))
			return stack;

		CompoundNBT morphData = stack.getTag().getCompound(TAG_MORPH_TOOL_DATA);
		return makeMorphedStack(stack, mod, morphData);
	}

	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, CompoundNBT morphData) {
		String currentMod = getModFromStack(currentStack);

		CompoundNBT currentCmp = new CompoundNBT();
		currentStack.write(currentCmp);
		currentCmp = currentCmp.copy();
		if(currentCmp.contains("tag"))
			currentCmp.getCompound("tag").remove(TAG_MORPH_TOOL_DATA);

		if(!currentMod.equalsIgnoreCase(MINECRAFT) && !currentMod.equalsIgnoreCase(MorphTool.MOD_ID))
			morphData.put(currentMod, currentCmp);

		ItemStack stack;
		if(targetMod.equals(MINECRAFT))
			stack = new ItemStack(ModItems.tool);
		else {
			CompoundNBT targetCmp = morphData.getCompound(targetMod);
			morphData.remove(targetMod);

			stack = ItemStack.read(targetCmp);
			if(stack.isEmpty())
				stack = new ItemStack(ModItems.tool);
		}

		if(!stack.hasTag())
			stack.setTag(new CompoundNBT());

		CompoundNBT stackCmp = stack.getTag();
		stackCmp.put(TAG_MORPH_TOOL_DATA, morphData);
		stackCmp.putBoolean(TAG_MORPHING_TOOL, true);

		if(stack.getItem() != ModItems.tool) {
			String displayName = ITextComponent.Serializer.toJson(stack.getDisplayName());
			if(stackCmp.contains(TAG_MORPH_TOOL_DISPLAY_NAME))
				displayName = stackCmp.getString(TAG_MORPH_TOOL_DISPLAY_NAME);
			else stackCmp.putString(TAG_MORPH_TOOL_DISPLAY_NAME, displayName);

			ITextComponent stackName = ITextComponent.Serializer.func_240643_a_(displayName).func_240699_a_(TextFormatting.GREEN);
			ITextComponent comp = new TranslationTextComponent("morphtool.sudo_name", stackName);
			stack.setDisplayName(comp);
		}

		stack.setCount(1);
		return stack;
	}

	private static final Map<String, String> modNames = new HashMap<String, String>();

	static {
		for(ModInfo modEntry : ModList.get().getMods())
			modNames.put(modEntry.getModId().toLowerCase(Locale.ENGLISH),  modEntry.getDisplayName());
	}

	public static String getModNameForId(String modId) {
		modId = modId.toLowerCase(Locale.ENGLISH);
		return modNames.containsKey(modId) ? modNames.get(modId) : modId;
	}

	public static boolean isMorphTool(ItemStack stack) {
		if(stack.isEmpty())
			return false;

		if(stack.getItem() == ModItems.tool)
			return true;

		return stack.hasTag() && stack.getTag().getBoolean(TAG_MORPHING_TOOL);
	}

	public static RayTraceResult raycast(Entity e, double len) {
		Vector3d vec = new Vector3d(e.getPosX(), e.getPosY(), e.getPosZ());
		if(e instanceof PlayerEntity)
			vec = vec.add(new Vector3d(0, e.getEyeHeight(), 0));

		Vector3d look = e.getLookVec();
		if(look == null)
			return null;

		return raycast(e, vec, look, len);
	}

	public static RayTraceResult raycast(Entity e, Vector3d origin, Vector3d ray, double len) {
		Vector3d end = origin.add(ray.normalize().scale(len));
		return e.getEntityWorld().rayTraceBlocks(new RayTraceContext(origin, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, e));
	}
}
