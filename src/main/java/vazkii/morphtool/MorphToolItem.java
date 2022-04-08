package vazkii.morphtool;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.arl.item.BasicItem;
import vazkii.arl.util.TooltipHandler;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MorphToolItem extends BasicItem {

	public MorphToolItem() {
		super("morphtool:tool", new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockState block = context.getLevel().getBlockState(context.getClickedPos());
		block.rotate(context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
		if (!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DATA)) {
			return;
		}

		CompoundTag data = stack.getTag().getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
		if (data.getAllKeys().size() == 0) {
			return;
		}

		List<String> tooltipList = new ArrayList<>();

		TooltipHandler.tooltipIfShift(tooltipList, () -> {
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

						tooltip.add(new TextComponent(" " + mod + " : " + name));
					}
				}
			}
		});

		tooltipList.forEach(tip -> tooltip.add(new TextComponent(tip)));
	}

}
