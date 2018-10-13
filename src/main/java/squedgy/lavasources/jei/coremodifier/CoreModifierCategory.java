package squedgy.lavasources.jei.coremodifier;


import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.jei.Plugin;
import squedgy.lavasources.jei.ingredients.EnergyIngredient;
import squedgy.lavasources.jei.ingredients.FluidRenderer;

import javax.annotation.Nullable;

public class CoreModifierCategory implements IRecipeCategory<CoreModifierWrapper> {

	public static String CORE_MODIFIER_CATEGORY = "lavasources.core_modifier";
	public static final int INPUT_SLOT = 0, OUTPUT_SLOT = 1, FLUID_SLOT = 0, ENERGY_SLOT = 0;
	private static IDrawable background = null, icon = null;

	@Override
	public String getUid() { return CORE_MODIFIER_CATEGORY; }

	@Override
	public String getTitle() {
		TextComponentTranslation translation = new TextComponentTranslation("jei.core_modifier");
		return translation.getUnformattedComponentText();
	}

	@Override
	public String getModName() { return LavaSources.NAME; }

	@Override
	public IDrawable getBackground() { return background; }

	@Nullable
	@Override
	public IDrawable getIcon() { return icon; }

	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, CoreModifierWrapper coreModifierWrapper, IIngredients iIngredients) {
		iRecipeLayout.getItemStacks().init(INPUT_SLOT, true, 17, 12);
		iRecipeLayout.getItemStacks().init(OUTPUT_SLOT, true, 37, 12);
		iRecipeLayout.getFluidStacks().init(FLUID_SLOT, true, new FluidRenderer(), 5,5, 6, 32,0,0);
		iRecipeLayout.getIngredientsGroup(EnergyIngredient.TYPE).init(ENERGY_SLOT, true, new EnergyIngredient.Renderer(), 61, 5, 6, 32,0,0);

		coreModifierWrapper.getIngredients(iIngredients);

		iRecipeLayout.getFluidStacks().set(FLUID_SLOT, iIngredients.getInputs(VanillaTypes.FLUID).get(FLUID_SLOT));
		iRecipeLayout.getItemStacks().set(INPUT_SLOT, iIngredients.getInputs(VanillaTypes.ITEM).get(INPUT_SLOT));
		iRecipeLayout.getItemStacks().set(OUTPUT_SLOT, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
		iRecipeLayout.getIngredientsGroup(EnergyIngredient.TYPE).set(ENERGY_SLOT, iIngredients.getInputs(EnergyIngredient.TYPE).get(ENERGY_SLOT));
	}

	public static void setDrawables(IJeiHelpers jeiHelper){
		background = Plugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(LavaSources.MOD_ID , "jei/guis.png"),0,0,72, 42 );
		icon = jeiHelper.getGuiHelper().createDrawableIngredient(ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.CORE_MODIFIER)).getDefaultInstance());
	}

}
