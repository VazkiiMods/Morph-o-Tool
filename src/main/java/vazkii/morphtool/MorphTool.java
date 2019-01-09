package vazkii.morphtool;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.arl.network.NetworkHandler;
import vazkii.morphtool.network.MessageMorphTool;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(modid = MorphTool.MOD_ID, name = MorphTool.MOD_NAME, version = MorphTool.VERSION, dependencies = MorphTool.DEPENDENCIES, guiFactory = MorphTool.GUI_FACTORY)
public class MorphTool {

	public static final String MOD_ID = "morphtool";
	public static final String MOD_NAME = "Morph-o-Tool";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;
	public static final String DEPENDENCIES = "required-before:autoreglib";
	public static final String GUI_FACTORY = "vazkii.morphtool.GuiFactory";

	@SidedProxy(clientSide = "vazkii.morphtool.proxy.ClientProxy", serverSide = "vazkii.morphtool.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		NetworkHandler.register(MessageMorphTool.class, Side.SERVER);
		proxy.preInit();
	}

}
