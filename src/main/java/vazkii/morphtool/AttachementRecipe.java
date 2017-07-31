package vazkii.morphtool;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.arl.recipe.ModRecipe;

public class AttachementRecipe extends ModRecipe {

	public AttachementRecipe() {
		super(new ResourceLocation("morphtool", "attachment"));
	}

	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		boolean foundTool = false;
		boolean foundTarget = false;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(isTarget(stack)) {
					if(foundTarget)
						return false;
					foundTarget = true;
				} else if(stack.getItem() == ModItems.tool) {
					if(foundTool)
						return false;
					foundTool = true;
				} else return false;
			}
		}

		return foundTool && foundTarget;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack target = ItemStack.EMPTY;

		for(int i = 0; i < var1.getSizeInventory(); i++) {
			ItemStack stack = var1.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.tool)
					tool = stack;
				else target = stack;
			}
		}

		ItemStack copy = tool.copy();
		NBTTagCompound cmp = copy.getTagCompound();
		if(cmp == null) {
			cmp = new NBTTagCompound();
			copy.setTagCompound(cmp);
		}

		if(!cmp.hasKey(MorphingHandler.TAG_MORPH_TOOL_DATA))
			cmp.setTag(MorphingHandler.TAG_MORPH_TOOL_DATA, new NBTTagCompound());

		NBTTagCompound morphData = cmp.getCompoundTag(MorphingHandler.TAG_MORPH_TOOL_DATA);
		String mod = MorphingHandler.getModFromStack(target);

		if(morphData.hasKey(mod))
			return ItemStack.EMPTY;

		NBTTagCompound modCmp = new NBTTagCompound();
		target.writeToNBT(modCmp);
		morphData.setTag(mod, modCmp);

		return copy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return false;
	}

	public boolean isTarget(ItemStack stack) {
		if(stack.isEmpty() || MorphingHandler.isMorphTool(stack))
			return false;

		String mod = MorphingHandler.getModFromStack(stack);
		if(mod.equals(MorphingHandler.MINECRAFT))
			return false;

		if(ConfigHandler.allItems)
			return true;

		if(ConfigHandler.blacklistedMods.contains(mod))
			return false;

		ResourceLocation registryNameRL = stack.getItem().getRegistryName();
		String registryName = registryNameRL.toString();
		if(ConfigHandler.whitelistedItems.contains(registryName) || ConfigHandler.whitelistedItems.contains(registryName + ":" + stack.getItemDamage()))
			return true;

		String itemName = registryNameRL.getResourcePath().toLowerCase();
		for(String s : ConfigHandler.whitelistedNames)
			if(itemName.contains(s.toLowerCase()))
				return true;

		return false;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}


}
