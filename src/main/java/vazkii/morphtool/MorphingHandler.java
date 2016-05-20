package vazkii.morphtool;

import net.minecraft.item.ItemStack;

public final class MorphingHandler {

	public static final MorphingHandler INSTANCE = new MorphingHandler();
	
	public static final String TAG_MORPHING_TOOL = "morphtool:is_morphing";
	
	public static boolean isMorphTool(ItemStack stack) {
		if(stack == null)
			return false;
		
		if(stack.getItem() == MorphTool.tool)
			return true;
		
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_MORPHING_TOOL);
	}
}
