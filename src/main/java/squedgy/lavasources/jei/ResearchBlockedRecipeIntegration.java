package squedgy.lavasources.jei;

import com.google.gson.JsonObject;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.JsonContext;
import squedgy.lavasources.crafting.recipes.ResearchBlockedRecipe;
import squedgy.lavasources.crafting.recipes.ResearchBlockedShapedRecipe;
import squedgy.lavasources.research.Research;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResearchBlockedRecipeIntegration<T extends ResearchBlockedRecipe> extends BlankRecipeWrapper{

	protected T recipe;

	private ResearchBlockedRecipeIntegration(T recipe){
		super();
		this.recipe = recipe;
	}


	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
		NonNullList<Ingredient> ingredients1 = recipe.getIngredients();
		List<List<ItemStack>> inputs = ingredients1.stream().map(Ingredient::getMatchingStacks).map(Arrays::asList).collect(Collectors.toList());
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
	}

	public static class ResearchBlockedShapedRecipeWrapper extends ResearchBlockedRecipeIntegration<ResearchBlockedShapedRecipe> implements IShapedCraftingRecipeWrapper {

		public ResearchBlockedShapedRecipeWrapper(ResearchBlockedShapedRecipe recipe) { super(recipe); }

		@Override
		public int getWidth() { return recipe.getRecipeWidth(); }

		@Override
		public int getHeight() { return recipe.getRecipeHeight(); }
	}

	public static class ResearchBlockedRecipeWrapper extends ResearchBlockedRecipeIntegration<ResearchBlockedRecipe>{

		public ResearchBlockedRecipeWrapper(ResearchBlockedRecipe recipe) { super(recipe); }
	}

	public static class HandlerAndFactory<T extends ResearchBlockedRecipe> implements IRecipeHandler<T>, IRecipeWrapperFactory<T> {

		@FunctionalInterface
		public interface Factory<T extends ResearchBlockedRecipe>{ IRecipeWrapper getWrapper(T recipe); }

		private Class<T> clazz;
		private Factory<T> factory;

		public HandlerAndFactory(Class<T> clazz, Factory<T> factory){
			this.clazz = clazz;
			this.factory = factory;
		}

		@Override
		public Class<T> getRecipeClass(){ return clazz; }

		@Override
		public String getRecipeCategoryUid(T recipe) { return VanillaRecipeCategoryUid.CRAFTING; }

		@Override
		public IRecipeWrapper getRecipeWrapper(T recipe) { return factory.getWrapper(recipe); }

		@Override
		public boolean isRecipeValid(ResearchBlockedRecipe recipe) {
			boolean ret = true;
			if(Minecraft.getMinecraft().world.isRemote) ret =  recipe.isPlayerCraftable(Minecraft.getMinecraft().player);
			return ret;
		}
	}
}
