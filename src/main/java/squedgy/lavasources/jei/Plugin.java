package squedgy.lavasources.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.api.jei.LavaSourcesJeiConstants;
import squedgy.lavasources.block.ModBlock;
import squedgy.lavasources.crafting.recipes.ResearchBlockedRecipe;
import squedgy.lavasources.crafting.recipes.ResearchBlockedShapedRecipe;
import squedgy.lavasources.enums.EnumEnergyTier;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.init.ModRegistries;
import squedgy.lavasources.jei.ResearchBlockedIntegration.*;
import squedgy.lavasources.jei.coremodifier.CoreModifierCategory;
import squedgy.lavasources.jei.coremodifier.CoreModifierWrapper.CoreModifierFactory;
import squedgy.lavasources.jei.ingredients.EnergyIngredient;
import squedgy.lavasources.jei.liquefier.LiquefierCategory;
import squedgy.lavasources.jei.liquefier.LiquefierWrapper;

import java.util.Arrays;

@JEIPlugin
public class Plugin implements IModPlugin {

	public static IJeiHelpers jeiHelper;

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
		registry.register(EnergyIngredient.TYPE, Arrays.asList(new EnergyIngredient(EnumEnergyTier.MASTER.CAPACITY, 0)), EnergyIngredient.HELPER, EnergyIngredient.RENDERER);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new CoreModifierCategory(registry.getJeiHelpers()));
		registry.addRecipeCategories(new LiquefierCategory(registry.getJeiHelpers()));
	}



	@Override
	public void register(IModRegistry registry) {
		jeiHelper = registry.getJeiHelpers();

		registry.handleRecipes(ResearchBlockedShapedRecipe.class, new HandlerAndFactory<>(ResearchBlockedShapedRecipe.class, ResearchBlockedShapedRecipeWrapper::new), VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(ResearchBlockedRecipe.class, new HandlerAndFactory<>(ResearchBlockedRecipe.class, ResearchBlockedRecipeWrapper::new), VanillaRecipeCategoryUid.CRAFTING);
		registry.handleRecipes(ICoreModifierRecipe.class, new CoreModifierFactory(), LavaSourcesJeiConstants.CORE_MODIFIER_CATEGORY);
		registry.handleRecipes(ILiquefierRecipe.class, new LiquefierWrapper.Factory(), LavaSourcesJeiConstants.LIQUEFIER_CATEGORY);

		registry.addRecipes(ModRegistries.CORE_MODIFIER_RECIPE_REGISTRY.getValuesCollection(), LavaSourcesJeiConstants.CORE_MODIFIER_CATEGORY);
		registry.addRecipes(ModRegistries.LIQUEFIER_RECIPE_REGISTRY.getValuesCollection(), LavaSourcesJeiConstants.LIQUEFIER_CATEGORY);;
		registry.addRecipeCatalyst(ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.CORE_MODIFIER)).getDefaultInstance(), LavaSourcesJeiConstants.CORE_MODIFIER_CATEGORY);
		registry.addRecipeCatalyst(ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.LIQUEFIER)).getDefaultInstance(), LavaSourcesJeiConstants.LIQUEFIER_CATEGORY);
	}

}
