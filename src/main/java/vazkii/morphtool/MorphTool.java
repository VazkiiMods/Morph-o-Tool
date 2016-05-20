package vazkii.morphtool;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(modid = MorphTool.MOD_ID, name = MorphTool.MOD_NAME, version = MorphTool.VERSION, dependencies = MorphTool.DEPENDENCIES)
public class MorphTool {

	public static final String MOD_ID = "Morphtool";
	public static final String MOD_NAME = "Morph-o-Tool";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	public static final String DEPENDENCIES = "required-after:Forge@[12.17.0.1909,);";
	
	@SidedProxy(clientSide = "vazkii.morphtool.proxy.ClientProxy", serverSide = "vazkii.morphtool.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static Item tool;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		tool = new ItemMorphTool();
		
		proxy.initModels();
		
		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}
	
}
