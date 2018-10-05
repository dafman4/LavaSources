package squedgy.lavasources.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;
import squedgy.lavasources.LavaSources;

public class RecipeHelper {

	/**
	 * {@link net.minecraftforge.oredict.ShapedOreRecipe}
	 * @param output - the itemstack to be output by the recipe
	 * @param params - input ingredients
	 */
	public static void registerShapedOreDictRecipe(ItemStack output, Object... params){
		registerRecipe(output, params, EnumRecipe.OREDICT_SHAPED);
	}

	/**
	 * {@link net.minecraft.item.crafting.ShapedRecipes}
	 * @param output - the itemstack to be output by the recipe
	 * @param params - input ingredients
	 */
	public static void registerShapedRecipe(ItemStack output, Object... params){
		registerRecipe(output, params, EnumRecipe.SHAPED);
	}

	/**
	 * {@link net.minecraftforge.oredict.ShapelessOreRecipe}
	 * @param output - the itemstack to be output by the recipe
	 * @param params - input ingredients
	 */
	public static void registerShapelessOreDictRecipe(ItemStack output, Object... params){
		registerRecipe(output, params, EnumRecipe.OREDICT_SHAPELESS);
	}

	/**
	 * {@link net.minecraft.item.crafting.ShapelessRecipes}
	 * @param output - the itemstack to be output by the recipe
	 * @param params - input ingredients
	 */
	public static void registerShapelessRecipe(ItemStack output, Object... params){
		registerRecipe(output, params, EnumRecipe.SHAPELESS);
	}

	private static void registerRecipe(ItemStack output, Object[] input, EnumRecipe type){
		ResourceLocation recipeLocation = getNameForRecipe(output);
		IRecipe recipe = null;
		if(type == EnumRecipe.OREDICT_SHAPED) recipe = new ShapedOreRecipe(recipeLocation, output, input);
		else if(type == EnumRecipe.SHAPED){
			CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(input);
			recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), primer.width, primer.height, primer.input, output);
		}
		else if (type == EnumRecipe.OREDICT_SHAPELESS) recipe = new ShapelessOreRecipe(recipeLocation, output, input);
		else{
			CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(input);
			recipe = new ShapelessRecipes(recipeLocation.getResourceDomain(), output, getInputList(input));
		}
		recipe.setRegistryName(recipeLocation);
		GameData.register_impl(recipe);
	}

	public static ResourceLocation getNameForRecipe(ItemStack output){
		ResourceLocation baseLocation = new ResourceLocation(LavaSources.MOD_ID, output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLocation = baseLocation;
		int recipeNumber = 0;
		while(CraftingManager.REGISTRY.containsKey(recipeLocation)){
			recipeNumber++;
			recipeLocation = new ResourceLocation(LavaSources.MOD_ID, baseLocation.getResourcePath() + "_" + recipeNumber);
		}
		return recipeLocation;
	}

	public static NonNullList<Ingredient> getInputList(Object[] input){
		NonNullList<Ingredient> ret = NonNullList.create();
		for(Object o : input){
			if(o instanceof Ingredient) ret.add((Ingredient)o);
			else{
				Ingredient ingredient = CraftingHelper.getIngredient(o);
				ret.add(ingredient == null? Ingredient.EMPTY : ingredient);
			}
		}
		return ret;
	}

	private enum EnumRecipe{
		OREDICT_SHAPELESS,
		OREDICT_SHAPED,
		SHAPELESS,
		SHAPED
	}

}
