package vazkii.morphtool;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.morphtool.proxy.CommonProxy;

@Mod(modid = MorphTool.MOD_ID, name = MorphTool.MOD_NAME, version = MorphTool.VERSION, dependencies = MorphTool.DEPENDENCIES, guiFactory = MorphTool.GUI_FACTORY)
public class MorphTool {

	public static final String MOD_ID = "Morphtool";
	public static final String MOD_NAME = "Morph-o-Tool";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	public static final String DEPENDENCIES = "required-after:Forge@[12.17.0.1909,);";
	public static final String GUI_FACTORY = "vazkii.morphtool.GuiFactory";

	@SidedProxy(clientSide = "vazkii.morphtool.proxy.ClientProxy", serverSide = "vazkii.morphtool.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static Item tool;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		tool = new ItemMorphTool();
		
		GameRegistry.addShapedRecipe(new ItemStack(tool), 
				" GB", " IR", "I  ",
				'G', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()),
				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
				'R', new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()),
				'I', new ItemStack(Items.IRON_INGOT));
		
		proxy.initModels();
		
		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}
	
}
