package vazkii.morphtool.proxy;

import net.minecraftforge.common.MinecraftForge;
import vazkii.morphtool.MorphingHandler;

public class CommonProxy {

	public void preInit() {
		MinecraftForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}
	
	public void updateEquippedItem() {
		// NO-OP
	}

}
