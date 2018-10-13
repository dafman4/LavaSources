package squedgy.lavasources.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import squedgy.lavasources.helper.RecipeHelper;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchUtil;

import java.util.ArrayList;

import static squedgy.lavasources.init.ModResearch.RegistryHandler.jsonHasAllMembers;

public class RecipeFactory<T extends IRecipe> implements IRecipeFactory {

	private RecipeReturnable<T> factory;

	public RecipeFactory(RecipeReturnable<T> factory){ this.factory = factory; }

	public static class ResearchReturn implements Returnable<Research> {
		@Override
		public Research getItem(int i, JsonArray arr) {
			if(isPrimitive(arr.get(i))) return ResearchUtil.getResearch(arr.get(i).getAsJsonPrimitive().getAsString());
			return null;
		}

		private boolean isPrimitive(JsonElement e) {
			if(!e.isJsonPrimitive()) throw new IllegalArgumentException("An element that should be json primitive is not! Element = " + e);
			return true;
		}
	}

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		if(jsonHasAllMembers(json, "pattern", "key", "result")){
			Research[] required = null;

			if(json.has("required")) required = getArrayFromJson(json, "required", new ResearchReturn());
			ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
			recipe.setRegistryName(RecipeHelper.getNameForRecipe(recipe.getRecipeOutput()));
			return factory.getRecipe(context, json, required);
		}else
			throw new IllegalArgumentException("The recipe provided didn't have one or more of the following members \"pattern, key, result\"\n\t\t\t");
	}

	@FunctionalInterface
	public interface RecipeReturnable<T extends IRecipe>{ T getRecipe(JsonContext context, JsonObject object, Research... r);}

	@FunctionalInterface
	public interface Returnable<T>{ T getItem(int i , JsonArray arr); }

	@SafeVarargs
	private final <U> U[] getArrayFromJson(JsonObject obj, String arrayMember, Returnable<U> returnable, U... empty){
		JsonArray arr = JsonUtils.getJsonArray(obj, arrayMember);
		ArrayList<U> ret = new ArrayList<>(arr.size());
		for(int i = 0; i < arr.size(); i++){
			ret.add(returnable.getItem(i, arr));
		}
		return ret.toArray(empty);
	}
}
