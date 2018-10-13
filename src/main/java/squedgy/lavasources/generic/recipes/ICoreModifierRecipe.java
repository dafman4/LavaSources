package squedgy.lavasources.generic.recipes;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ICoreModifierRecipe extends IForgeRegistryEntry<ICoreModifierRecipe> {

	/**
	 * Should return the core that gets output
	 *
	 * @return ItemStack representing the core that is made
	 */
	public abstract ItemStack getOutput();

	/**
	 * A fluid stack that contains the entire amount, as well as the
	 * fluid that is required to make the output item
	 *
	 * @return a FluidStack representing the fluid, and the amount that is required to make the output
	 */
	public abstract FluidStack getRequiredFluid();

	/**
	 * The amount of energy that is required to make it
	 *
	 * @return the amount of energy required to make the output
	 */
	public abstract int getRequiredEnergy();

}
