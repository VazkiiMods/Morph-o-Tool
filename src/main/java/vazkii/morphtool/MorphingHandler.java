package vazkii.morphtool;

import com.typesafe.config.Config;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();
	
	public static final String MINECRAFT = "minecraft";
	
	public static final String TAG_MORPHING_TOOL = "morphtool:is_morphing";
	public static final String TAG_MORPH_TOOL_DATA = "morphtool:data";
	public static final String TAG_MORPH_TOOL_DISPLAY_NAME = "morphtool:displayName";

	@SubscribeEvent
	public void onPlayerTick(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			ItemStack mainHandItem = player.getHeldItemMainhand();

			if(isMorphTool(mainHandItem)) {
				RayTraceResult res = raycast(player, 4.5);
				if(res != null) {
					IBlockState state = player.worldObj.getBlockState(res.getBlockPos());
					String mod = getModFromState(state);
					
					ItemStack newStack = getShiftStackForMod(mainHandItem, mod);
					if(newStack != mainHandItem && !ItemStack.areItemsEqual(newStack, mainHandItem)) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
						MorphTool.proxy.updateEquippedItem();
					}
				}
			}
		}
	}
	
	public static String getModFromState(IBlockState state) {
		return getModOrAlias(state.getBlock().getRegistryName().getResourceDomain());
	}
	
	public static String getModFromStack(ItemStack stack) {
		return getModOrAlias(stack == null ? MINECRAFT : stack.getItem().getRegistryName().getResourceDomain());
	}
	
	public static String getModOrAlias(String mod) {
		return ConfigHandler.aliases.containsKey(mod) ? ConfigHandler.aliases.get(mod) : mod;
	}
	
	public static ItemStack getShiftStackForMod(ItemStack stack, String mod) {
		if(!stack.hasTagCompound())
			return stack;
	
		String currentMod = getModFromStack(stack);
		if(mod.equals(currentMod))
			return stack;
		
		NBTTagCompound morphData = stack.getTagCompound().getCompoundTag(TAG_MORPH_TOOL_DATA);
		return makeMorphedStack(stack, mod, morphData);
	}
	
	public static ItemStack makeMorphedStack(ItemStack currentStack, String targetMod, NBTTagCompound morphData) {
		String currentMod = getModFromStack(currentStack);
		
		NBTTagCompound currentCmp = new NBTTagCompound();
		currentStack.writeToNBT(currentCmp);
		currentCmp = (NBTTagCompound) currentCmp.copy();
		if(currentCmp.hasKey("tag"))
			currentCmp.getCompoundTag("tag").removeTag(TAG_MORPH_TOOL_DATA);
		morphData.setTag(currentMod, currentCmp);
		
		ItemStack stack;
		if(targetMod.equals(MINECRAFT))
			stack = new ItemStack(MorphTool.tool);
		else {
			NBTTagCompound targetCmp = morphData.getCompoundTag(targetMod);
			morphData.removeTag(targetMod);
			
			stack = ItemStack.loadItemStackFromNBT(targetCmp);
			if(stack == null)
				stack = new ItemStack(MorphTool.tool);
		}
		
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound stackCmp = stack.getTagCompound();
		stackCmp.setTag(TAG_MORPH_TOOL_DATA, morphData);
		stackCmp.setBoolean(TAG_MORPHING_TOOL, true);
		
		if(stack.getItem() != MorphTool.tool) {
			String displayName = stack.getDisplayName();
			if(stackCmp.hasKey(TAG_MORPH_TOOL_DISPLAY_NAME))
				displayName = stackCmp.getString(TAG_MORPH_TOOL_DISPLAY_NAME);
			else stackCmp.setString(TAG_MORPH_TOOL_DISPLAY_NAME, displayName);
			
			stack.setStackDisplayName(TextFormatting.RESET + I18n.format("morphtool.sudo_name", TextFormatting.GREEN + displayName + TextFormatting.RESET));
		}
		
		return stack;
	}
	
	public static boolean isMorphTool(ItemStack stack) {
		if(stack == null)
			return false;
		
		if(stack.getItem() == MorphTool.tool)
			return true;
		
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_MORPHING_TOOL);
	}
	
	public static RayTraceResult raycast(Entity e, double len) {
		Vec3d vec = new Vec3d(e.posX, e.posY, e.posZ);
		if(e instanceof EntityPlayer)
			vec = vec.add(new Vec3d(0, e.getEyeHeight(), 0));
		
		Vec3d look = e.getLookVec();
		if(look == null)
			return null;

		return raycast(e.worldObj, vec, look, len);
	}
	
	public static RayTraceResult raycast(World world, Vec3d origin, Vec3d ray, double len) {
		Vec3d end = origin.add(ray.normalize().scale(len));
		RayTraceResult pos = world.rayTraceBlocks(origin, end);
		return pos;
	}
}
