package squedgy.lavasources.jei.liquefier;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.jei.ModCategory;
import squedgy.lavasources.jei.ingredients.LiquefierFluidRenderer;

import static squedgy.lavasources.api.constants.LavaSourcesConstants.Jei.LIQUEFIER_CATEGORY;

public class LiquefierCategory extends ModCategory<LiquefierWrapper> {

	public LiquefierCategory(IJeiHelpers helper) {
		super(helper, new TextComponentTranslation("jei.liquefier"), LIQUEFIER_CATEGORY);
	}


	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, LiquefierWrapper iLiquefierRecipe, IIngredients iIngredients) {
			iRecipeLayout.getItemStacks().init(0, true,4, 11 );
			iRecipeLayout.getFluidStacks().init(0 ,true, new LiquefierFluidRenderer(), 25, 5, 30, 32, 0, 0);
			iLiquefierRecipe.getIngredients(iIngredients);
			iRecipeLayout.getFluidStacks().set(0, iIngredients.getOutputs(VanillaTypes.FLUID).get(0));
			iRecipeLayout.getItemStacks().set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
	}

	@Override
	protected void setBackground(IJeiHelpers helper) {
		background = helper.getGuiHelper().createDrawable(new ResourceLocation(LavaSources.MOD_ID , "jei/guis.png"), 0, 43, 60, 42);
	}

	@Override
	protected void setIcon(IJeiHelpers helper) {
		icon = helper.getGuiHelper().createDrawableIngredient(ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.LIQUEFIER)).getDefaultInstance());
	}
}
