package vazkii.morphtool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

public class MorphRecipeSerializer {

    public static final RecipeSerializer<AttachementRecipe> ATTACHMENT = new SimpleRecipeSerializer<>(AttachementRecipe::new);

    @Mod.EventBusSubscriber(modid = MorphTool.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void onRecipeSerializerRegistry(final RegistryEvent.Register<RecipeSerializer<?>> event) {
            IForgeRegistry<RecipeSerializer<?>> registry = event.getRegistry();
            registry.register(ATTACHMENT.setRegistryName(new ResourceLocation(MorphTool.MOD_ID, "attachment")));
        }
    }
}
