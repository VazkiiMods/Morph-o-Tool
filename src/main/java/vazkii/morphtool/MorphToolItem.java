package vazkii.morphtool;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Rotation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import vazkii.arl.item.BasicItem;
import vazkii.arl.util.TooltipHandler;

import net.minecraft.item.Item.Properties;

public class MorphToolItem extends BasicItem {

	public MorphToolItem() {
		super("morphtool:tool", new Properties().stacksTo(1).tab(ItemGroup.TAB_TOOLS));
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		BlockState block = context.getLevel().getBlockState(context.getClickedPos());
		block.rotate(context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		if(!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DATA))
			return;

		CompoundNBT data = stack.getTag().getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
		if(data.getAllKeys().size() == 0)
			return;

		List<String> tooltipList = new ArrayList<>();

		TooltipHandler.tooltipIfShift(tooltipList, () -> {
			for(String s : data.getAllKeys()) {
				CompoundNBT cmp = data.getCompound(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.of(cmp);
					if(!stack.isEmpty()) {
						String name = modStack.getHoverName().getString();
						if(modStack.hasTag() && modStack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME))
							name = ((CompoundNBT) modStack.getTag().get(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME)).getString("text");
						String mod = MorphingHandler.getModFromStack(modStack);

						tooltip.add(new StringTextComponent(" " + mod + " : " + name));
					}
				}
			}
		}
		);

		tooltipList.forEach(tip -> tooltip.add(new StringTextComponent(tip)));
	}

}
