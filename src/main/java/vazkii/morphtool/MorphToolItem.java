package vazkii.morphtool;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.morphtool.data_components.ToolContentComponent;

import java.util.List;

public class MorphToolItem extends Item {

	public MorphToolItem(Properties properties) {
		super(properties.stacksTo(1).component(MorphToolRegistries.IS_MORPH_TOOL, false).component(MorphToolRegistries.TOOL_CONTENT, ToolContentComponent.EMPTY));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState block = context.getLevel().getBlockState(context.getClickedPos());
		if (level.setBlock(pos, block.rotate(level, pos, Rotation.CLOCKWISE_90), Block.UPDATE_ALL)) {
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.has(MorphToolRegistries.TOOL_CONTENT))
			return;

		ToolContentComponent contents = stack.get(MorphToolRegistries.TOOL_CONTENT);
		if (contents == null || contents.isEmpty())
			return;
		if (Screen.hasShiftDown()) {
			for (ItemStack contentStack : contents.getItems()) {
				if (!contentStack.isEmpty()) {
					Component name;
					if (contentStack.has(MorphToolRegistries.OG_DISPLAY_NAME)) {
						name = contentStack.get(MorphToolRegistries.OG_DISPLAY_NAME);
					} else {
						name = contentStack.getHoverName();
					}

					String mod = MorphingHandler.getModFromStack(contentStack);
					tooltip.add(Component.literal(" " + mod + " : " + name.getString()));
				}
			}
		} else {
			tooltip.add(Component.translatable(MorphTool.MOD_ID + ".misc.shift_for_info"));
		}
	}

}
