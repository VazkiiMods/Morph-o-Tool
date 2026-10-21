package vazkii.morphtool.common;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import vazkii.morphtool.MorphToolMod;
import vazkii.morphtool.data_components.MorphingDataComponent;

import java.util.function.Supplier;

public final class MorphToolRegistries {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MorphToolMod.MOD_ID);
	public static final DeferredItem<Item> MORPH_TOOL = ITEMS.registerItem("tool", MorphToolItem::new, new Item.Properties().stacksTo(1));

	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MorphToolMod.MOD_ID);
	public static final Supplier<DataComponentType<MorphingDataComponent>> MORPH_DATA = DATA_COMPONENTS.registerComponentType("morph_data", builder -> builder.persistent(MorphingDataComponent.CODEC).networkSynchronized(MorphingDataComponent.STREAM_CODEC).cacheEncoding());

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MorphToolMod.MOD_ID);
	public static final Supplier<RecipeSerializer<AttachementRecipe>> ATTACHMENT = RECIPE_SERIALIZERS.register("attachment", () -> new SimpleCraftingRecipeSerializer<>(AttachementRecipe::new));

}
