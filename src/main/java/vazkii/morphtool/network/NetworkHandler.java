package vazkii.morphtool.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import vazkii.morphtool.MorphTool;

public class NetworkHandler {
    private static SimpleChannel channel;
    private static int id = 0;

    public static void register() {
        final String protocolVersion = "1";
        channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MorphTool.MOD_ID, "main"))
                .networkProtocolVersion(() -> protocolVersion)
                .clientAcceptedVersions(protocolVersion::equals)
                .serverAcceptedVersions(protocolVersion::equals)
                .simpleChannel();
        channel.registerMessage(id++, MessageMorphTool.class, MessageMorphTool::serialize, MessageMorphTool::deserialize, MessageMorphTool::handle);
    }

    public static <MSG> void sendToServer(MSG msg) {
        channel.sendToServer(msg);
    }

}
