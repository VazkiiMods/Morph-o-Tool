package vazkii.morphtool;

import net.minecraft.item.Item;

public final class ModItems {

	public static Item tool;
	
	public static void init() {
		tool = new ItemMorphTool();
	}
	
}
