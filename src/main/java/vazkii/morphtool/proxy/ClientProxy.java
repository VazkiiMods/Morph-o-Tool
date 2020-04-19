package vazkii.morphtool.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import vazkii.morphtool.ClientHandler;
import vazkii.morphtool.ConfigHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(ClientHandler.INSTANCE);
	}

	@Override
	public void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemRenderer.resetEquippedProgress(ConfigHandler.invertHandShift.get() ? Hand.OFF_HAND : Hand.MAIN_HAND);
	}

}
