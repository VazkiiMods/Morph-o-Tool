package vazkii.morphtool.client;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import vazkii.morphtool.MorphToolMod;
import vazkii.morphtool.common.MorphHandler;
import vazkii.morphtool.common.MorphToolRegistries;
import vazkii.morphtool.common.MorphingHandler;

@Mod(value = MorphToolMod.MOD_ID, dist = Dist.CLIENT)
public class MorphToolModClient {

	public MorphToolModClient(IEventBus bus, ModContainer modContainer) {
		bus.addListener(this::addToCreativeTab);

		NeoForge.EVENT_BUS.register(MorphHandler.INSTANCE); //TODO check if this really needs to be called here?
		NeoForge.EVENT_BUS.register(MorphHandlerClient.INSTANCE);

		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(MorphToolRegistries.MORPH_TOOL);
		}
	}
}
