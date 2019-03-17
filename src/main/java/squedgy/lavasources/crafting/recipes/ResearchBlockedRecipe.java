package squedgy.lavasources.crafting.recipes;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.crafting.CraftingUtils;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.research.Research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResearchBlockedRecipe<T extends IRecipe> extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	public static final RecipeFactory.RecipeReturnable<ResearchBlockedRecipe<ShapelessOreRecipe>> RESEARCH_BLOCKED_FACTORY = (context, object, r) -> new ResearchBlockedRecipe(((context1, object1, r1) -> ShapelessOreRecipe.factory(context1, object1)), context, object, r);
	protected final List<Research> BLOCKED_BY = new ArrayList<>();
	protected final T RECIPE;

	public ResearchBlockedRecipe(Factory.RecipeReturnable<T> factory, JsonContext context, JsonObject obj, Research... required){
		RECIPE = factory.getRecipe(context, obj);
		BLOCKED_BY.addAll(Arrays.asList(required));
	}

	public boolean isPlayerCraftable(EntityPlayer player){
		boolean ret = true;
		if (player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)) {
			IPlayerResearchCapability cap = player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
			for(Research r : BLOCKED_BY)if(!cap.hasResearch(r))ret = false;
		}else{
			ret = false;
		}
		return ret;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		boolean flag = true;
		if(world != null && world.isRemote) {
			try {
				EntityPlayer p = CraftingUtils.getPlayerIfPossible(inventoryCrafting);
				flag = isPlayerCraftable(p);
			} catch (Exception e) {
				LavaSources.writeMessage(getClass(), "there was an issue finding the player crafting!\nthis might not be LavaSources fault!\nerror: " + e.getLocalizedMessage());
				flag = false;
			}
		}
		return flag && RECIPE.matches(inventoryCrafting, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) { return RECIPE.getCraftingResult(inventoryCrafting); }

	@Override
	public boolean canFit(int i, int i1) { return RECIPE.canFit(i, i1); }

	@Override
	public ItemStack getRecipeOutput() { return RECIPE.getRecipeOutput(); }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) { return RECIPE.getRemainingItems(inv); }

	@Override
	public NonNullList<Ingredient> getIngredients() { return RECIPE.getIngredients(); }

	@Override
	public boolean isDynamic() { return RECIPE.isDynamic(); }

	@Override
	public String getGroup() { return RECIPE.getGroup(); }

	public static class Factory extends RecipeFactory<ResearchBlockedRecipe<ShapelessOreRecipe>> {

		public Factory() { super(RESEARCH_BLOCKED_FACTORY); }

	}
}
