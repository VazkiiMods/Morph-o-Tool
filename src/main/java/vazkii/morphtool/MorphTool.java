package vazkii.morphtool;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import vazkii.morphtool.network.MessageMorphTool;
import vazkii.morphtool.proxy.ClientProxy;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(MorphTool.MOD_ID)
@EventBusSubscriber(modid = MorphTool.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MorphTool {
	public static final String MOD_ID = "morphtool";
	public static CommonProxy proxy;

	public MorphTool(IEventBus bus, ModContainer modContainer) {
		Registries.DATA_COMPONENTS.register(bus);
		Registries.ITEMS.register(bus);
		Registries.SERIALIZERS.register(bus);

		modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = new CommonProxy();
		proxy.preInit();
	}

	@SubscribeEvent
	public static void registerPayloadHandler(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.playToServer(
				MessageMorphTool.TYPE,
				MessageMorphTool.STREAM_CODEC,
				new MessageMorphTool.Handler()
		);
	}
}
