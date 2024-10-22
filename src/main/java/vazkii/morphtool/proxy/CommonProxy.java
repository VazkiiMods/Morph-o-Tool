package vazkii.morphtool.proxy;

import net.neoforged.neoforge.common.NeoForge;

import vazkii.morphtool.MorphingHandler;

public class CommonProxy {

	public void preInit() {
		NeoForge.EVENT_BUS.register(MorphingHandler.INSTANCE);
	}

	public void updateEquippedItem() {
		// NO-OP
	}

}
