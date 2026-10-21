package vazkii.morphtool.common;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import vazkii.morphtool.MorphToolConfig;
import vazkii.morphtool.data_components.MorphingDataComponent;

import java.util.HashMap;
import java.util.Map;

public final class MorphHandler {
    public static final MorphHandler INSTANCE = new MorphHandler();
    public static final String MINECRAFT = "minecraft";

    @SubscribeEvent
    public void onItemDropped(ItemTossEvent event) {

    }

    @SubscribeEvent
    public void onItemBroken(PlayerDestroyItemEvent event) {

    }

    public static ItemStack createStackOnScroll(MorphingDataComponent data, ItemStack currentStack, int nextIndex) {
        var mutable = new MorphingDataComponent.Mutable(data);
        var nextStack = mutable.turnToNextItem(currentStack, nextIndex);
        if (!nextStack.isEmpty()) {
            nextStack.set(MorphToolRegistries.MORPH_DATA, mutable.toImmutable());

            if (!nextStack.is(MorphToolRegistries.MORPH_TOOL.get())) {
                Component hoverName = nextStack.getHoverName();
                Component stackName = Component.literal(hoverName.getString()).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GREEN));
                Component comp = Component.translatable("morphtool.sudo_name", stackName);
                nextStack.set(DataComponents.CUSTOM_NAME, comp);
            }

            nextStack.setCount(1);
            return nextStack;
        }
        return ItemStack.EMPTY;
    }


    /**
     * Checks if the given item is an untransformed Morph Tool or an item that the Morph Tool has turned into.
     */
    public static boolean isMorphToolOrMorphed(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(MorphToolRegistries.MORPH_TOOL.get())) {
            return true;
        }

        return stack.has(MorphToolRegistries.MORPH_DATA); //Only Morph Tool or Items inside the Morph Tool should have this data.
    }

    public static ItemStack getItemFromCorrectMainHand(Player player) {
        return player.getItemInHand(MorphToolConfig.invertHandShift.get() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
    }

    public static String getModFromState(BlockState state) {
        var modOfBlock = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace();
        return getModOrAlias(modOfBlock);
    }

    //TODO Previously used alias too, idk if that's necessary
    public static String getDirectModFromStack(ItemStack stack) {
        return stack.getItem().getCreatorModId(stack);
    }

    public static String getModOrAliasFromStack(ItemStack stack) {
        String modId = stack.getItem().getCreatorModId(stack);
        return getModOrAlias(modId);
    }

    /** TODO rethink this <br>
     * A block from source mod should be compatible with item from source and alias
     * A block from alias should be compatible with item from alias and source
     * <br>
     * Is this the case rn??
     *
     */
    public static String getModOrAlias(String mod) {
        Map<String, String> aliases = new HashMap<>();

        for (String s : MorphToolConfig.aliasesList.get()) {
            if (s.matches(".+?=.+")) {
                String[] tokens = s.toLowerCase().split("=");
                aliases.put(tokens[0], tokens[1]);
            }
        }

        return aliases.getOrDefault(mod, mod);
    }
}
