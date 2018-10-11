package squedgy.lavasources.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import scala.actors.threadpool.Arrays;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.crafting.CraftingUtils;
import squedgy.lavasources.generic.IModCraftable;
import squedgy.lavasources.helper.RecipeHelper;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchUtil;

import java.util.ArrayList;
import java.util.List;

public class ResearchBlockedRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private final List<Research> BLOCKED_BY = new ArrayList<>();
	private final ShapedOreRecipe RECIPE;

	public ResearchBlockedRecipe(Research[] blockedBy, ShapedOreRecipe recipe){
		this.BLOCKED_BY.addAll(Arrays.asList(blockedBy));
		this.RECIPE = recipe;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public boolean isPlayerCraftable(EntityPlayer player){
		if(ModCapabilities.PLAYER_RESEARCH_CAPABILITY != null)
			if (player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)) {
				IPlayerResearchCapability cap = player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
				for (Research r : BLOCKED_BY) if (!cap.hasResearch(r)) return false;
				return true;
			}
		return false;
	}

	@Override
	public int getRecipeWidth() {
		return RECIPE.getRecipeWidth();
	}

	@Override
	public int getRecipeHeight() {
		return RECIPE.getRecipeHeight();
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		try {
			EntityPlayer pl = CraftingUtils.getPlayerIfPossible(inv);
			if(pl != null && isPlayerCraftable(pl)) return RECIPE.matches(inv, worldIn);
		} catch (Exception e) {
			LavaSources.writeMessage(getClass(), "There was an issue retrieving the player crafting this recipe: this is potentially not LavaSources fault");
			LavaSources.writeMessage(getClass(), e.getMessage());
			LavaSources.writeMessage(getClass(), "recipe = " + RECIPE.getRegistryName());
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return RECIPE.getCraftingResult(inv);
	}

	@Override
	public boolean canFit(int width, int height) {
		return RECIPE.canFit(width, height);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return RECIPE.getRecipeOutput();
	}

//</editor-fold>

	public static final class Factory implements IRecipeFactory{

		private class ResearchReturn implements Returnable<Research>{
			@Override
			public Research getItem(int i, JsonArray arr) {
				LavaSources.writeMessage(getClass(), "trying to get research from " + arr.get(i));
				if(isPrimitive(arr.get(i))){
					return ResearchUtil.getResearch(arr.get(i).getAsJsonPrimitive().getAsString());
				}
				return null;
			}
		}

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			LavaSources.writeMessage(getClass(), "reading texture " + json);
			if(json.has("pattern") && json.has("key") && json.has("result")){
				Research[] required = new Research[0];

				if(json.has("required")){
					required = getArrayFromJson(json, "required", new ResearchReturn());
				}
				ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
				recipe.setRegistryName(RecipeHelper.getNameForRecipe(recipe.getRecipeOutput()));
				return new ResearchBlockedRecipe(required, recipe);
			}else
			throw new IllegalArgumentException("The recipe provided didn't have one of the following members \"pattern, key, result\"");
		}

		@FunctionalInterface
		private interface Returnable<T>{ T getItem(int i , JsonArray arr); }

		@SafeVarargs
		private final <T> T[] getArrayFromJson(JsonObject obj, String arrayNode, Returnable<T> returnable, T... empty){
			JsonArray arr = JsonUtils.getJsonArray(obj, arrayNode);
			ArrayList<T> ret = new ArrayList<>(arr.size());
			for(int i = 0; i < arr.size(); i++){
				ret.add(returnable.getItem(i, arr));
			}
			return ret.toArray(empty);
		}

		private boolean isPrimitive(JsonElement e) {
			if(!e.isJsonPrimitive()) throw new IllegalArgumentException("An element that should be json primitive is not!");
			return true;
		}
	}
}
