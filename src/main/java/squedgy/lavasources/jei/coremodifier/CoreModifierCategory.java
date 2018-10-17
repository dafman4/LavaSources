package squedgy.lavasources.jei.coremodifier;


import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.jei.ModCategory;
import squedgy.lavasources.jei.ingredients.EnergyIngredient;
import squedgy.lavasources.jei.ingredients.CoreModifierFluidRenderer;

import static squedgy.lavasources.api.jei.LavaSourcesJeiConstants.CORE_MODIFIER_CATEGORY;

public class CoreModifierCategory extends ModCategory<CoreModifierWrapper> {

	public static final int INPUT_SLOT = 0, OUTPUT_SLOT = 1, FLUID_SLOT = 0, ENERGY_SLOT = 0;

	public CoreModifierCategory(IJeiHelpers helper) {
		super(helper, new TextComponentTranslation("jei.core_modifier"), CORE_MODIFIER_CATEGORY);
	}

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, CoreModifierWrapper coreModifierWrapper, IIngredients iIngredients) {
		iRecipeLayout.getItemStacks().init(INPUT_SLOT, true, 17, 12);
		iRecipeLayout.getItemStacks().init(OUTPUT_SLOT, true, 37, 12);
		iRecipeLayout.getFluidStacks().init(FLUID_SLOT, true, new CoreModifierFluidRenderer(), 5,5, 6, 32,0,0);
		iRecipeLayout.getIngredientsGroup(EnergyIngredient.TYPE).init(ENERGY_SLOT, true, new EnergyIngredient.Renderer(), 61, 5, 6, 32,0,0);

		coreModifierWrapper.getIngredients(iIngredients);

		iRecipeLayout.getFluidStacks().set(FLUID_SLOT, iIngredients.getInputs(VanillaTypes.FLUID).get(FLUID_SLOT));
		iRecipeLayout.getItemStacks().set(INPUT_SLOT, iIngredients.getInputs(VanillaTypes.ITEM).get(INPUT_SLOT));
		iRecipeLayout.getItemStacks().set(OUTPUT_SLOT, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
		iRecipeLayout.getIngredientsGroup(EnergyIngredient.TYPE).set(ENERGY_SLOT, iIngredients.getInputs(EnergyIngredient.TYPE).get(ENERGY_SLOT));
	}

	@Override
	protected void setBackground(IJeiHelpers helper) {
		background = helper.getGuiHelper().createDrawable(new ResourceLocation(LavaSources.MOD_ID , "jei/guis.png"),0,0,72, 42 );
	}

	@Override
	protected void setIcon(IJeiHelpers helper) {
		icon = helper.getGuiHelper().createDrawableIngredient(ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.CORE_MODIFIER)).getDefaultInstance());
	}
}
