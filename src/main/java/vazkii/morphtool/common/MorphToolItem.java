package vazkii.morphtool.common;

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

import vazkii.morphtool.MorphToolMod;
import vazkii.morphtool.data_components.MorphingDataComponent;
import vazkii.morphtool.data_components.MorphingStackWrapper;
import vazkii.morphtool.data_components.ToolContentComponent;

import java.util.List;

public class MorphToolItem extends Item {

	public MorphToolItem(Properties properties) {
		super(properties.stacksTo(1).component(MorphToolRegistries.MORPH_DATA, MorphingDataComponent.DEFAULT));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState block = context.getLevel().getBlockState(context.getClickedPos());
		if (level.setBlock(pos, block.rotate(level, pos, Rotation.CLOCKWISE_90), Block.UPDATE_ALL)) {
			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.has(MorphToolRegistries.MORPH_DATA)) return;

		MorphingDataComponent data = stack.get(MorphToolRegistries.MORPH_DATA);
		if (data == null || data.isEmpty())
			return;
		if (Screen.hasShiftDown()) {
			data.getMorphingWrappers().forEach(wrapper -> {
				var mod = wrapper.getModId();
				var name = wrapper.getOgDisplayName();

				tooltip.add(Component.literal(" " + mod + " : " + name.getString()));
			});
		} else {
			tooltip.add(Component.translatable(MorphToolMod.MOD_ID + ".misc.shift_for_info"));
		}
	}

}
