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

	public static ResourceLocation getNameForRecipe(ItemStack output){
		ResourceLocation baseLocation = new ResourceLocation(LavaSources.MOD_ID, output.getItem().getRegistryName().getResourcePath());
		return getNameForRecipe(baseLocation);
	}

	public static ResourceLocation getNameForRecipe(ResourceLocation baseLocation){
		ResourceLocation recipeLocation = baseLocation;
		int recipeNumber = 0;
		while(CraftingManager.REGISTRY.containsKey(recipeLocation)){
			recipeNumber++;
			recipeLocation = new ResourceLocation(LavaSources.MOD_ID, baseLocation.getResourcePath() + "_" + recipeNumber);
		}
		return recipeLocation;

	}

}
