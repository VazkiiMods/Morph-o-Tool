package vazkii.morphtool.proxy;

import net.minecraftforge.common.MinecraftForge;
import vazkii.morphtool.ClientHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
	}

	@Override
	public void updateEquippedItem() {
		//Minecraft.getInstance().getItemRenderer().resetEquippedProgress(ConfigHandler.invertHandShift ? Hand.OFF_HAND : Hand.MAIN_HAND);
	}

}
