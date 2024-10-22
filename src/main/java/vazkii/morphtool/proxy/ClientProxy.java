package vazkii.morphtool.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.common.NeoForge;

import vazkii.morphtool.ClientHandler;
import vazkii.morphtool.ConfigHandler;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		NeoForge.EVENT_BUS.register(ClientHandler.INSTANCE);
	}

	@Override
	public void updateEquippedItem() {
		Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(ConfigHandler.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
	}

}
