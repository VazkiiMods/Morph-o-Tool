package vazkii.morphtool;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.morphtool.data_components.ToolContentComponent;

import java.util.function.Supplier;

public final class Registries {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MorphTool.MOD_ID);
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MorphTool.MOD_ID);
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MorphTool.MOD_ID);

	public static final Supplier<DataComponentType<Boolean>> IS_MORPH_TOOL = DATA_COMPONENTS.registerComponentType("is_morph_tool", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
	public static final Supplier<DataComponentType<ToolContentComponent>> TOOL_CONTENT = DATA_COMPONENTS.registerComponentType("tool_content", builder -> builder.persistent(ToolContentComponent.CODEC).networkSynchronized(ToolContentComponent.STREAM_CODEC));

	public static final DeferredItem<Item> MORPH_TOOL = ITEMS.registerItem("tool", MorphToolItem::new, new Item.Properties().stacksTo(1));

	public static final Supplier<RecipeSerializer<AttachementRecipe>> ATTACHMENT = SERIALIZERS.register("attachment", () -> new SimpleCraftingRecipeSerializer<>(AttachementRecipe::new));

}
