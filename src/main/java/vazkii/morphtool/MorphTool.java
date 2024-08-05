package vazkii.morphtool;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.morphtool.network.NetworkHandler;
import vazkii.morphtool.proxy.ClientProxy;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(MorphTool.MOD_ID)
public class MorphTool {
	public static final String MOD_ID = "morphtool";
	public static CommonProxy proxy;

	public MorphTool() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);

		Registries.ITEMS.register(bus);
		Registries.SERIALIZERS.register(bus);

		bus.addListener(this::addToCreativeTab);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.preInit();
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.register();
	}

	private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Registries.MORPH_TOOL);
		}
	}

}
