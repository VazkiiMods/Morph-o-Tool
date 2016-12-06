package vazkii.morphtool.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
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
		Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress(ConfigHandler.invertHandShift ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
	}

}
