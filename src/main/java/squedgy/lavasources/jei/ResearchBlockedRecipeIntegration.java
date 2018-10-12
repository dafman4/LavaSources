package squedgy.lavasources.jei;

import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.crafting.recipes.ResearchBlockedRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResearchBlockedRecipeIntegration extends BlankRecipeWrapper {

	private ResearchBlockedRecipeIntegration(ResearchBlockedRecipe recipe){
		super();
		this.recipe = recipe;
	}

	private ResearchBlockedRecipe recipe;

	@Override
	public void getIngredients(IIngredients ingredients) {
		LavaSources.writeMessage(getClass(), "setting ingredients");
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
		LavaSources.writeMessage(getClass(), "output= " + ingredients.getOutputs(VanillaTypes.ITEM));
		List<ItemStack> inputs = recipe.getIngredients().stream().map(Ingredient::getMatchingStacks).map(r -> r.length > 0 ? r[0] : ItemStack.EMPTY).collect(Collectors.toList());
		LavaSources.writeMessage(getClass(), "input lists = " + inputs);
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
	}


	public static class HandlerAndFactory implements IRecipeHandler<ResearchBlockedRecipe>, IRecipeWrapperFactory<ResearchBlockedRecipe> {


		@Override
		public Class<ResearchBlockedRecipe> getRecipeClass() {
			LavaSources.writeMessage(getClass(), "returning recipe class");
			return ResearchBlockedRecipe.class;
		}

		@Override
		public String getRecipeCategoryUid(ResearchBlockedRecipe recipe) {
			LavaSources.writeMessage(getClass(), "returning recipe category Uid");
			return VanillaRecipeCategoryUid.CRAFTING;
		}

		@Override
		public IRecipeWrapper getRecipeWrapper(ResearchBlockedRecipe recipe) {
			LavaSources.writeMessage(getClass(), "returning recipe wrapper");
			return new ResearchBlockedRecipeIntegration(recipe);
		}

		@Override
		public boolean isRecipeValid(ResearchBlockedRecipe recipe) {
			boolean ret = true;
			if(Minecraft.getMinecraft().world.isRemote) ret =  recipe.isPlayerCraftable(Minecraft.getMinecraft().player);
			LavaSources.writeMessage(getClass(), "checking if recipe's valid, returning = " + ret);
			return ret;
		}
	}
}
