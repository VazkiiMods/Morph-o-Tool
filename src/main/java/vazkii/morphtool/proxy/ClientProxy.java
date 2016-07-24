package vazkii.morphtool.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import vazkii.morphtool.ConfigHandler;
import vazkii.morphtool.MorphTool;

public class ClientProxy extends CommonProxy {

	@Override
	public void initModels() {
		ModelLoader.setCustomModelResourceLocation(MorphTool.tool, 0, new ModelResourceLocation(MorphTool.tool.getRegistryName(), "inventory"));
	}

	@Override
	public void updateEquippedItem() {
		Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress(ConfigHandler.invertHandShift ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
	}

}
