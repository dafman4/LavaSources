package squedgy.lavasources.crafting.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;

public class CoreModifierRecipe extends IForgeRegistryEntry.Impl<ICoreModifierRecipe> implements ICoreModifierRecipe {


	protected static final int MINIMUM_FLUID_AMOUNT = 1000, MAXIMUM_FLUID_AMOUNT = 12000, MAXIMUM_ENERGY_REQUIRED = 10000, MINIMUM_ENERGY_REQUIRED = 3000;
	private final ItemStack OUTPUT;
	private final FluidStack REQUIRED_FLUID;
	private final int REQUIRED_ENERGY;

	public CoreModifierRecipe(ResourceLocation registryName, ItemStack output, FluidStack requiredFluid, int requiredEnergy){
		setRegistryName(registryName);
		if(requiredEnergy > MAXIMUM_ENERGY_REQUIRED) requiredEnergy = MAXIMUM_ENERGY_REQUIRED;
		if(requiredEnergy < MINIMUM_ENERGY_REQUIRED) requiredEnergy = MINIMUM_ENERGY_REQUIRED;
		if(requiredFluid.amount > MAXIMUM_FLUID_AMOUNT) requiredFluid.amount = MAXIMUM_FLUID_AMOUNT;
		if(requiredFluid.amount < MINIMUM_FLUID_AMOUNT) requiredFluid.amount = MINIMUM_FLUID_AMOUNT;
		this.OUTPUT = output;
		this.REQUIRED_ENERGY = requiredEnergy;
		this.REQUIRED_FLUID = requiredFluid;
	}

	@Override
	public ItemStack getOutput() { return OUTPUT.copy(); }

	@Override
	public FluidStack getRequiredFluid() { return REQUIRED_FLUID.copy(); }

	@Override
	public int getRequiredEnergy() { return REQUIRED_ENERGY; }

}
