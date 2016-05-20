package vazkii.morphtool;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ItemMorphTool extends Item {

	public ItemMorphTool() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);

		setUnlocalizedName("morphtool:tool");
		GameRegistry.register(this, new ResourceLocation("morphtool:tool"));

		GameRegistry.addRecipe(new AttachementRecipe());
		RecipeSorter.register("morphtool:attachment", AttachementRecipe.class, Category.SHAPELESS, "");
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		boolean rotated = block.rotateBlock(worldIn, pos, facing);

		return rotated ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(MorphingHandler.TAG_MORPH_TOOL_DATA))
			return;

		NBTTagCompound data = stack.getTagCompound().getCompoundTag(MorphingHandler.TAG_MORPH_TOOL_DATA);
		if(data.getKeySet().size() == 0)
			return;

		if(!GuiScreen.isShiftKeyDown())
			tooltip.add(I18n.format("morphtool.hold_shift"));
		else for(String s : data.getKeySet()) {
			NBTTagCompound cmp = data.getCompoundTag(s);
			if(cmp != null) {
				ItemStack modStack = ItemStack.loadItemStackFromNBT(cmp);
				if(modStack != null) {
					String name = modStack.getDisplayName();
					if(modStack.hasTagCompound() && modStack.getTagCompound().hasKey(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME))
						name = modStack.getTagCompound().getString(MorphingHandler.TAG_MORPH_TOOL_DISPLAY_NAME);

					tooltip.add(" " + s + " : " + name);
				}
			}
		}
	}

}
