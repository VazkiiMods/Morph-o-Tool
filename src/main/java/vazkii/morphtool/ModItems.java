package vazkii.morphtool;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MorphTool.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModItems {

	public static Item tool = new ItemMorphTool();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt) {
		evt.getRegistry().register(tool);
	}

}
