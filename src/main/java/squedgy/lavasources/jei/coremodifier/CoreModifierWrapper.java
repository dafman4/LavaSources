package squedgy.lavasources.jei.coremodifier;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import squedgy.lavasources.enums.EnumEnergyTier;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.jei.ingredients.EnergyIngredient;

import java.util.Arrays;

public class CoreModifierWrapper implements IRecipeWrapper {
	private ICoreModifierRecipe recipe;

	private CoreModifierWrapper(ICoreModifierRecipe recipe){ this.recipe = recipe; }

	@Override
	public void getIngredients(IIngredients iIngredients) {
		iIngredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(Arrays.asList(ModItems.EMPTY_CORE.getDefaultInstance())));
		iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
		iIngredients.setInput(VanillaTypes.FLUID, recipe.getRequiredFluid());
		iIngredients.setInput(EnergyIngredient.TYPE, new EnergyIngredient(EnumEnergyTier.MASTER.CAPACITY, recipe.getRequiredEnergy()));
	}

	public static class CoreModifierFactory implements IRecipeWrapperFactory<ICoreModifierRecipe> {

		@Override
		public IRecipeWrapper getRecipeWrapper(ICoreModifierRecipe iCoreModifierRecipe) {
			return new CoreModifierWrapper(iCoreModifierRecipe);
		}
	}
}
