package vazkii.morphtool.proxy;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.morphtool.ModItems;
import vazkii.morphtool.MorphingHandler;

public class CommonProxy {

	public void preInit() {
		ModItems.init();
		
		RecipeHandler.addShapedRecipe(new ItemStack(ModItems.tool),
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
