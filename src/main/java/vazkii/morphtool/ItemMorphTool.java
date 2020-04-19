package vazkii.morphtool;

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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.arl.item.BasicItem;
import vazkii.arl.util.TooltipHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemMorphTool extends BasicItem {

	public ItemMorphTool() {
		super("morphtool:tool", new Properties().maxStackSize(1).group(ItemGroup.TOOLS));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		BlockState block = context.getWorld().getBlockState(context.getPos());
		block.rotate(context.getWorld(), context.getPos(), Rotation.CLOCKWISE_90);
		return super.onItemUse(context);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
		if(!stack.hasTag() || !stack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DATA))
			return;

		CompoundNBT data = stack.getTag().getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
		if(data.keySet().size() == 0)
			return;

		List<String> tooltipList = new ArrayList<>();

		TooltipHandler.tooltipIfShift(tooltipList, () -> {
			for(String s : data.keySet()) {
				CompoundNBT cmp = data.getCompound(s);
				if(cmp != null) {
					ItemStack modStack = ItemStack.read(cmp);
					if(!stack.isEmpty()) {
						String name = modStack.getDisplayName().getString();
						if(modStack.hasTag() && modStack.getTag().contains(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME))
							name = modStack.getTag().getString(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME);

						tooltip.add(new TranslationTextComponent(" " + MorphingHandler.getModNameForId(s) + " : " + name));
					}
				}
			}
		}
		);

		tooltipList.forEach(tip -> tooltip.add(new StringTextComponent(tip)));
	}

}
