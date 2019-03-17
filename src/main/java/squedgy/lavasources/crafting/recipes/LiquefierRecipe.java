package squedgy.lavasources.crafting.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.advancements.critereon.OredictItemPredicate;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import squedgy.lavasources.helper.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class LiquefierRecipe extends Impl<ILiquefierRecipe> implements ILiquefierRecipe{
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private final FluidStack fluidCreated;
	private final ItemStack[] input;
	private final int output;

	public LiquefierRecipe(ResourceLocation name, FluidStack fluidCreated, OreIngredient input, int output){
		setRegistryName(name);
		if(fluidCreated==null) fluidCreated = FluidRegistry.getFluidStack(FluidRegistry.LAVA.getName(), 0);
		this.fluidCreated = fluidCreated;
		this.input = (input != null) ? input.getMatchingStacks() : null;;
		this.output = output;

	}

	public LiquefierRecipe(String name, FluidStack fluidCreated, OreIngredient input, int output){
		this(StringUtils.getResourceLocation(LavaSources.MOD_ID, name), fluidCreated, input, output);
	}


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Overrides">

	@Nullable
	@Override
	public FluidStack getOutput(ItemStack input) { return new FluidStack(fluidCreated.getFluid(),hasInput(input) ? output : 0); }

	@Nullable
	@Override
	public ItemStack[] getInputs() { return input; }

	@Nonnull
	@Override
	public FluidStack getFluidMade() { return fluidCreated.copy(); }

	@Override
	public boolean hasInput(ItemStack i){ return input != null && Arrays.stream(input).anyMatch(i::isItemEqual); }

//<editor-fold>


	@Override
	public String toString() {
		return "LiquefierRecipe{" +
				"fluidCreated=[" + fluidCreated.getFluid().getName() + "]" +
				", input=" + Arrays.toString(input) +
				", output=" + output +
				'}';
	}
}
