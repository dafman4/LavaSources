package squedgy.lavasources.jei.liquefier;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;

import java.util.*;

public class LiquefierWrapper implements IRecipeWrapper {

	private final ILiquefierRecipe recipe;

	public LiquefierWrapper(ILiquefierRecipe recipe){
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients iIngredients) {
		List<ItemStack> inputs = Arrays.asList(recipe.getInputs());
		List<FluidStack> stacks = new ArrayList<>();
		iIngredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(inputs));
		for(ItemStack s : inputs) stacks.add(recipe.getOutput(s));
		stacks = new ArrayList<>(new HashSet<>(stacks));
		iIngredients.setOutputLists(VanillaTypes.FLUID, Arrays.asList(stacks));
	}

	public static class Factory implements IRecipeWrapperFactory<ILiquefierRecipe>{

		@Override
		public IRecipeWrapper getRecipeWrapper(ILiquefierRecipe recipe) { return new LiquefierWrapper(recipe); }
	}

}
