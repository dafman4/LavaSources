package squedgy.lavasources.init;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squedgy.lavasources.LavaSources;

public class ModRecipes {
	private ModRecipes(){}
	private static ResourceLocation getResourceLocation(String s) { return new ResourceLocation(LavaSources.MOD_ID, s); }

	@Mod.EventBusSubscriber
	public static class RegistryHandler{

		@SubscribeEvent
		public static void registerRecipes(RegistryEvent<IRecipe> event){ }

		public static void register(){ }

	}
}
