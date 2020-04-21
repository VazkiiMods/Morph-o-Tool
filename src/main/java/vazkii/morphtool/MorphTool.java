package vazkii.morphtool;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import vazkii.arl.network.NetworkHandler;
import vazkii.morphtool.network.MessageMorphTool;
import vazkii.morphtool.proxy.ClientProxy;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(MorphTool.MOD_ID)
public class MorphTool {

	public static final String MOD_ID = "morphtool";
	public static NetworkHandler NETWORKHANDLER;
	public static CommonProxy proxy;

	public MorphTool(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.CONFIG_SPEC);

		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.preInit();
		
		ModItems.init();

		NETWORKHANDLER = new NetworkHandler(MOD_ID, 1);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		NETWORKHANDLER.register(MessageMorphTool.class, NetworkDirection.PLAY_TO_SERVER);
	}

}
