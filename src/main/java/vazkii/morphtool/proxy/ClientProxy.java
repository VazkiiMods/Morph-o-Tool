package vazkii.morphtool.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
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
		Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

}
