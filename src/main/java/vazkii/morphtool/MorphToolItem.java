package vazkii.morphtool;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

import javax.annotation.Nullable;
import java.util.List;

public class MorphToolItem extends Item {

	public MorphToolItem() {
		super(new Properties().stacksTo(1));
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
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DATA)) {
			return;
		}

		CompoundTag data = stack.getTag().getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
		if (data.getAllKeys().isEmpty()) {
			return;
		}

		if (Screen.hasShiftDown()) {
			for (String s : data.getAllKeys()) {
				CompoundTag cmp = data.getCompound(s);
				if (cmp != null) {
					ItemStack modStack = ItemStack.of(cmp);
					if (!stack.isEmpty()) {
						String name = modStack.getHoverName().getString();
						if (modStack.hasTag() && modStack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME)) {
							CompoundTag rawName = ((CompoundTag) modStack.getTag().get(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME));
							Component nameComp = Component.Serializer.fromJson(rawName.getString("text"));
							if(nameComp != null)
								name = nameComp.getString();
						}
						String mod = MorphingHandler.getModFromStack(modStack);

						tooltip.add(Component.literal(" " + mod + " : " + name));
					}
				}
			}
		} else {
			tooltip.add(Component.translatable(MorphTool.MOD_ID + ".misc.shift_for_info"));
		}
	}

}
