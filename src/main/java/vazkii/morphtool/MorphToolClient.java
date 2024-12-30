package vazkii.morphtool;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import vazkii.morphtool.proxy.ClientProxy;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(value = MorphTool.MOD_ID, dist = Dist.CLIENT)
public class MorphToolClient {
	public static CommonProxy proxy;

	public MorphToolClient(IEventBus bus, ModContainer modContainer) {
		bus.addListener(this::addToCreativeTab);

		proxy = new ClientProxy();
		proxy.preInit();
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Registries.MORPH_TOOL);
		}
	}
}
