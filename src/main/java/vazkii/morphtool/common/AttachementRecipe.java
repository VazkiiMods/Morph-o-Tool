package vazkii.morphtool.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import vazkii.morphtool.MorphToolConfig;
import vazkii.morphtool.data_components.MorphingDataComponent;
import vazkii.morphtool.data_components.ToolContentComponent;

public class AttachementRecipe extends CustomRecipe {

	public AttachementRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
	}

	@Override
	@NotNull
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return NonNullList.withSize(input.size(), ItemStack.EMPTY);
	}

	@Override
	public boolean matches(CraftingInput input, Level var2) {
		boolean foundTool = false;
		boolean foundTarget = false;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (isTarget(stack)) {
					if (foundTarget) {
						return false;
					}
					foundTarget = true;
				} else if (stack.is(MorphToolRegistries.MORPH_TOOL.get())) {
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
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack target = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				//Always check for the item here and not for the component! (We don't want a morphed item to be used in the recipe)
				if (stack.is(MorphToolRegistries.MORPH_TOOL.get())) {
					tool = stack;
				} else {
					target = stack;
				}
			}
		}

		if (!tool.has(MorphToolRegistries.MORPH_DATA)) return ItemStack.EMPTY;

		ItemStack copy = tool.copy();
		MorphingDataComponent data = copy.get(MorphToolRegistries.MORPH_DATA);
		if (data == null) { //Should never be the case because of previous check
			return ItemStack.EMPTY;
		}

		MorphingDataComponent.Mutable mutable = new MorphingDataComponent.Mutable(data);
		if (target.isEmpty() || !mutable.tryAddNewItem(target)) {
			return ItemStack.EMPTY;
		}
		copy.set(MorphToolRegistries.MORPH_DATA, mutable.toImmutable());

		return copy;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public boolean isTarget(ItemStack stack) {
		if (stack.isEmpty() || MorphHandler.isMorphToolOrMorphed(stack)) {
			return false;
		}

		String mod = MorphHandler.getDirectModFromStack(stack);
		if (mod.equals(MorphHandler.MINECRAFT)) { //Not allowed because looking at vanilla blocks should turn Tool back to normal
			return false;
		}

		if (MorphToolConfig.allItems.get()) {
			return true;
		}

		if (MorphToolConfig.blacklistedMods.get().contains(mod)) {
			return false;
		}

		ResourceLocation registryNameRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
		String registryName = registryNameRL.toString();
		if (MorphToolConfig.whitelistedItems.get().contains(registryName) || MorphToolConfig.whitelistedItems.get().contains(registryName + ":" + stack.getDamageValue())) {
			return true;
		}

		String itemName = registryNameRL.getPath().toLowerCase();
		for (String s : MorphToolConfig.whitelistedNames.get()) {
			if (itemName.contains(s.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MorphToolRegistries.ATTACHMENT.get();
	}

}
