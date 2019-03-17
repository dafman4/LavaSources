package squedgy.lavasources.crafting.recipes;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squedgy.lavasources.research.Research;

public class ResearchBlockedShapedRecipe extends ResearchBlockedRecipe<ShapedOreRecipe> implements IShapedRecipe {

	public static final Factory.RecipeReturnable<ResearchBlockedShapedRecipe> RESEARCH_BLOCKED_SHAPED_FACTORY = (context, object, r) -> new ResearchBlockedShapedRecipe(((context1, object1, r1) -> ShapedOreRecipe.factory(context1, object1)), context, object, r);

	public ResearchBlockedShapedRecipe(Factory.RecipeReturnable<ShapedOreRecipe> factors, JsonContext context, JsonObject obj, Research... required) {
		super(factors, context, obj, required);
	}

	@Override
	public int getRecipeWidth() {
		return RECIPE.getRecipeWidth();
	}

	@Override
	public int getRecipeHeight() {
		return RECIPE.getRecipeHeight();
	}

	public static class Factory extends RecipeFactory<ResearchBlockedShapedRecipe> {

		public Factory() {
			super(RESEARCH_BLOCKED_SHAPED_FACTORY);
		}

	}
}
