package vazkii.morphtool;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import org.slf4j.Logger;

import vazkii.morphtool.common.MorphHandler;
import vazkii.morphtool.common.MorphToolRegistries;
import vazkii.morphtool.network.NetworkHandler;

@Mod(MorphToolMod.MOD_ID)
public class MorphToolMod {
	public static final String MOD_ID = "morphtool";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MorphToolMod(IEventBus bus, ModContainer modContainer) {
		bus.addListener(NetworkHandler::registerPayloadHandler);

		MorphToolRegistries.DATA_COMPONENTS.register(bus);
		MorphToolRegistries.ITEMS.register(bus);
		MorphToolRegistries.RECIPE_SERIALIZERS.register(bus);

		modContainer.registerConfig(ModConfig.Type.COMMON, MorphToolConfig.CONFIG_SPEC);

		NeoForge.EVENT_BUS.register(MorphHandler.INSTANCE);
	}

}
