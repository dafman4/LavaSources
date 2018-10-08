
package squedgy.lavasources.enums;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.capabilities.FluidHandler;
import squedgy.lavasources.capabilities.CapacityAndStorageWrapper;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.helper.EnumConversions;

// Author David
public enum EnumFluidTier {
	BASIC(4000, 200, 1000/EnumConversions.SECONDS_TO_TICKS.convertToInt(10), 10),
	INTERMEDIATE(10000, 500, 1000/EnumConversions.SECONDS_TO_TICKS.convertToInt(7), 10),
	ADVANCED(16000, 900, 1000/EnumConversions.SECONDS_TO_TICKS.convertToInt(4), 10),
	MASTER(32000, 1400, 1000/EnumConversions.SECONDS_TO_TICKS.convertToInt(1), 10);

	public final int CAPACITY, MAX_TRANSFER, GENERATED, REQUIRED;

	EnumFluidTier(int capacity, int maxTransfer, int generated, int required){
		this.CAPACITY = capacity;
		this.MAX_TRANSFER = maxTransfer;
		this.GENERATED = generated;
		this.REQUIRED = required;
	}

	public FluidHandler getFluidHandler(boolean sharedTank, ModFluidTank tank, ModFluidTank... extraTanks){
		int capacity = 1, stored = 0;
		if(sharedTank){
			capacity = tank.getCapacity();
			stored = tank.getFluidAmount();
			for(ModFluidTank t : extraTanks ){
				capacity += tank.getCapacity();
				stored += tank.getFluidAmount();
			}
		}
		FluidHandler ret = new FluidHandler(sharedTank, capacity ,stored);
        ret.addFluidTank(tank);
        for(ModFluidTank t: extraTanks) ret.addFluidTank(t);
		return ret;
	}

	public FluidHandler getFluidHandler(ModFluidTank tank){return getFluidHandler(false ,tank);}
	
	public ModFluidTank getFluidTank(Fluid fluid){ return getFluidTank(new FluidStack(fluid, 0), false); }

	public ModFluidTank getFluidTank(FluidStack currentHeld, boolean acceptAllFluids){ return getFluidTank(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, currentHeld, acceptAllFluids ? ModFluidTank.ACCEPT_ALL_FLUIDS : new FluidStack[] {}); }
	
	public ModFluidTank getFluidTank(FluidStack currentHeld, FluidStack[] extraAcceptedFluids){ return getFluidTank(CAPACITY, MAX_TRANSFER, MAX_TRANSFER, currentHeld, extraAcceptedFluids); }
	
	public ModFluidTank getFluidTank(int capacity, int maxReceive, int maxExtract, FluidStack currentHeld, FluidStack[] extraAcceptedFluids){ return new ModFluidTank(new CapacityAndStorageWrapper(capacity, currentHeld.amount), currentHeld, maxReceive, maxExtract, extraAcceptedFluids); }
	
	public static EnumFluidTier getTier(EnumUpgradeTier tier){ return EnumFluidTier.values()[tier.LEVEL]; }

}
