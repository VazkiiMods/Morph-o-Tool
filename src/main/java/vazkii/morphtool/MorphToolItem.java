package vazkii.morphtool;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MorphToolItem extends Item {

	public MorphToolItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		/*TODO decide if this feature should be kept. The below code doesn't work since 1.19, no one has complained so far tho.
		BlockState block = context.getLevel().getBlockState(context.getClickedPos());
		block.rotate(context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);

		 */
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
