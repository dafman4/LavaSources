package squedgy.lavasources.generic.recipes;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.List;

public interface ILiquefierRecipe extends IForgeRegistryEntry<ILiquefierRecipe> {

	/**
	 * Gets a FluidStack representing how much fluid is made from 1 input item(s)
	 * @return FluidStack containing how much fluid is created
	 */
	public abstract @Nonnull FluidStack getOutput(ItemStack input);

	/**
	 * Returns the input to create the output...
	 * @return ItemStack array of all accepted Inputs for the output fluid
	 */
	public abstract @Nonnull ItemStack[] getInputs();

	/**
	 * This method returns the fluid made from this recipe
	 * @return FluidStack containing the Fluid this recipe makes
	 */
	public abstract @Nonnull FluidStack getFluidMade();

	public abstract boolean hasInput(ItemStack i);

}
