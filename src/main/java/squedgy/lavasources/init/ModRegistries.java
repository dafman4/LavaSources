package squedgy.lavasources.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;
import squedgy.lavasources.research.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

public class ModRegistries {

	public static IForgeRegistry<Research> RESEARCH_REGISTRY;
	public static IForgeRegistry<GuiLocation> GUI_LOCATION_REGISTRY;
	public static IForgeRegistry<ResearchTab> RESEARCH_TAB_REGISTRY;
	public static IForgeRegistry<ICoreModifierRecipe> CORE_MODIFIER_RECIPE_REGISTRY;
	public static IForgeRegistry<ILiquefierRecipe> LIQUEFIER_RECIPE_REGISTRY;
	public static IForgeRegistry<GuiLocation.TextureWrapper> TEXTURE_WRAPPER_REGISTRY;

	@Mod.EventBusSubscriber(modid = LavaSources.MOD_ID)
	public static class RegistryHandler{

		public static void preInit(){}

	}

}
