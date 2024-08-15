package vazkii.morphtool;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import vazkii.morphtool.data_components.ToolContentComponent;

import java.util.ArrayList;
import java.util.List;

public class AttachementRecipe extends CustomRecipe {

	public AttachementRecipe(CraftingBookCategory pCategory) {
		super(pCategory);
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
				} else if (stack.is(Registries.MORPH_TOOL.get())) {
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
				if (stack.is(Registries.MORPH_TOOL.get())) {
					tool = stack;
				} else {
					target = stack;
				}
			}
		}

		if (!tool.has(Registries.TOOL_CONTENT)) return ItemStack.EMPTY;
		ItemStack copy = tool.copy();
		String mod = MorphingHandler.getModFromStack(target);
		ToolContentComponent contents = copy.get(Registries.TOOL_CONTENT);
		List<ItemStack> contentStacks = new ArrayList<>(List.copyOf(copy.get(Registries.TOOL_CONTENT).contents()));

		//This assures that only one item of a mod is in the tool
		if (!contentStacks.isEmpty()) {
			for (ItemStack contentStack : contentStacks) {
				if (BuiltInRegistries.ITEM.getKey(contentStack.getItem()).getNamespace().equals(mod)) {
					return ItemStack.EMPTY;
				}
			}
		}

		contentStacks.add(target);

		copy.set(Registries.TOOL_CONTENT, new ToolContentComponent(contentStacks));

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

		ResourceLocation registryNameRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
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
	public RecipeSerializer<?> getSerializer() {
		return Registries.ATTACHMENT.get();
	}

}
