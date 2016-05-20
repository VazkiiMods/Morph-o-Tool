package vazkii.morphtool;

import net.minecraft.block.Block;
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

public class ItemMorphTool extends Item {

	public ItemMorphTool() {
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.MISC);
		
		setUnlocalizedName("morphtool:tool");
		GameRegistry.register(this, new ResourceLocation("morphtool:tool"));
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		boolean rotated = block.rotateBlock(worldIn, pos, facing);
		
		// TODO DEBUG
		NBTTagCompound cmp = new NBTTagCompound();
		cmp.setBoolean(MorphingHandler.TAG_MORPHING_TOOL, true);
		NBTTagCompound dataCmp = new NBTTagCompound();
		
		NBTTagCompound psiCmp = new NBTTagCompound();
		playerIn.inventory.getStackInSlot(1).writeToNBT(psiCmp);
		dataCmp.setTag("psi", psiCmp);
		
		NBTTagCompound quarkCmp = new NBTTagCompound();
		playerIn.inventory.getStackInSlot(2).writeToNBT(quarkCmp);
		dataCmp.setTag("quark", quarkCmp);
		
		cmp.setTag(MorphingHandler.TAG_MORPH_TOOL_DATA, dataCmp);
		stack.setTagCompound(cmp);
		
		return rotated ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}
	
}
