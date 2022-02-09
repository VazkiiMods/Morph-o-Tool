package vazkii.morphtool;

import net.minecraft.world.item.Item;

public final class ModItems {

	public static Item tool;
	
	public static void init() {
		tool = new MorphToolItem();
	}

}
