package vazkii.morphtool;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModItems {

	public static Item tool = null;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRegistryInit(RegisterEvent.RegisterHelper<?> event) {
		if(tool == null)
			tool = new MorphToolItem();
	}

}
