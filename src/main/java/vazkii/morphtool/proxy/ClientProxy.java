package vazkii.morphtool.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import vazkii.morphtool.MorphTool;

public class ClientProxy extends CommonProxy {

	public void initModels() {
        ModelLoader.setCustomModelResourceLocation(MorphTool.tool, 0, new ModelResourceLocation(MorphTool.tool.getRegistryName(), "inventory"));
	}
	
}
