package squedgy.lavasources.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

public class ModRegistries {

	public static IForgeRegistry<Research> RESEARCH_REGISTRY;
	public static IForgeRegistry<GuiLocation> GUI_LOCATION_REGISTRY;
	public static IForgeRegistry<ResearchTab> RESEARCH_TAB_REGISTRY;
	public static IForgeRegistry<ICoreModifierRecipe> CORE_MODIFIER_RECIPE_REGISTRY;

	@Mod.EventBusSubscriber(modid = LavaSources.MOD_ID)
	public static class RegistryHandler{

		public static void preInit(){}

	}

}
