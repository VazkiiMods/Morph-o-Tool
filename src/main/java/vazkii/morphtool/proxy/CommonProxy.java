package vazkii.morphtool.proxy;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.morphtool.ItemMorphTool;
import vazkii.morphtool.MorphTool;
import vazkii.morphtool.MorphingHandler;

public class CommonProxy {

	public void preInit() {
		MorphTool.tool = new ItemMorphTool();

		GameRegistry.addShapedRecipe(new ItemStack(MorphTool.tool),
				" GB", " IR", "I  ",
				'G', new ItemStack(Items.DYE, 1, EnumDyeColor.GREEN.getDyeDamage()),
				'B', new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()),
				'R', new ItemStack(Items.DYE, 1, EnumDyeColor.RED.getDyeDamage()),
				'I', new ItemStack(Items.IRON_INGOT));

		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}
	
	public void updateEquippedItem() {
		// NO-OP
	}

}
