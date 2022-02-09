package vazkii.morphtool;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AttachementRecipe extends CustomRecipe {

	public AttachementRecipe(ResourceLocation idIn) {
		super(idIn);
	}

	@Override
	public boolean matches(CraftingContainer var1, Level var2) {
		boolean foundTool = false;
		boolean foundTarget = false;

		for (int i = 0; i < var1.getContainerSize(); i++) {
			ItemStack stack = var1.getItem(i);
			if (!stack.isEmpty()) {
				if (isTarget(stack)) {
					if (foundTarget) {
						return false;
					}
					foundTarget = true;
				} else if (stack.getItem() == ModItems.tool) {
					if (foundTool) {
						return false;
					}
					foundTool = true;
				} else {
					return false;
				}
			}
		}

		return foundTool && foundTarget;
	}

	@Override
	public ItemStack assemble(CraftingContainer var1) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < var1.getContainerSize(); i++) {
			ItemStack stack = var1.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == ModItems.tool) {
					tool = stack;
				} else {
					target = stack;
				}
			}
		}

		ItemStack copy = tool.copy();
		CompoundTag cmp = copy.getTag();
		if (cmp == null) {
			cmp = new CompoundTag();
			copy.setTag(cmp);
		}

		if (!cmp.contains(MorphingHandler.TAG_MORPH_TOOL_DATA)) {
			cmp.put(MorphingHandler.TAG_MORPH_TOOL_DATA, new CompoundTag());
		}

		CompoundTag morphData = cmp.getCompound(MorphingHandler.TAG_MORPH_TOOL_DATA);
		String mod = MorphingHandler.getModFromStack(target);

		if (morphData.contains(mod)) {
			return ItemStack.EMPTY;
		}

		CompoundTag modCmp = new CompoundTag();
		target.save(modCmp);
		morphData.put(mod, modCmp);

		return copy;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public boolean isTarget(ItemStack stack) {
		if (stack.isEmpty() || MorphingHandler.isMorphTool(stack)) {
			return false;
		}

		String mod = MorphingHandler.getModFromStack(stack);
		if (mod.equals(MorphingHandler.MINECRAFT)) {
			return false;
		}

		if (ConfigHandler.allItems.get()) {
			return true;
		}

		if (ConfigHandler.blacklistedMods.get().contains(mod)) {
			return false;
		}

		ResourceLocation registryNameRL = stack.getItem().getRegistryName();
		String registryName = registryNameRL.toString();
		if (ConfigHandler.whitelistedItems.get().contains(registryName) || ConfigHandler.whitelistedItems.get().contains(registryName + ":" + stack.getDamageValue())) {
			return true;
		}

		String itemName = registryNameRL.getPath().toLowerCase();
		for (String s : ConfigHandler.whitelistedNames.get()) {
			if (itemName.contains(s.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MorphRecipeSerializer.ATTACHMENT;
	}

}
