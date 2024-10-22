package vazkii.morphtool;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import vazkii.morphtool.network.NetworkHandler;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(MorphTool.MOD_ID)
public class MorphTool {
	public static final String MOD_ID = "morphtool";
	public static CommonProxy proxy;

	public MorphTool(IEventBus bus, ModContainer modContainer) {
		bus.addListener(NetworkHandler::registerPayloadHandler);

		Registries.DATA_COMPONENTS.register(bus);
		Registries.ITEMS.register(bus);
		Registries.SERIALIZERS.register(bus);

		modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = new CommonProxy();
		proxy.preInit();
	}

}
