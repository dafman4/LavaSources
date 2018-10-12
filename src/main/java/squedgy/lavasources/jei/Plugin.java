package squedgy.lavasources.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.crafting.recipes.ResearchBlockedRecipe;

import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class Plugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		List<ResearchBlockedRecipe> recipes = GameRegistry.findRegistry(IRecipe.class).getEntries().stream().filter(r -> r.getValue() instanceof ResearchBlockedRecipe).map(r -> (ResearchBlockedRecipe) r.getValue()).collect(Collectors.toList());
		LavaSources.writeMessage(getClass(), "\n\n\n\n\t\tregistering recipes: " + recipes + "\n\n\n\n");
		registry.handleRecipes(ResearchBlockedRecipe.class, new ResearchBlockedRecipeIntegration.HandlerAndFactory(), VanillaRecipeCategoryUid.CRAFTING);
//		registry.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);

	}

}
