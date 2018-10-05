package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.EnumConversions;

import javax.annotation.Nullable;

public class ModFluidTank implements IFluidHandler, IFluidTank, IFluidTankProperties, INBTSerializable<NBTTagCompound> {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	public static final FluidStack[] ACCEPT_ALL_FLUIDS = null;
	private FluidStack fluid;
	private int maxDrain, maxFill;
	private FluidTankWrapper info;
	private FluidStack[] acceptedFluids;
	private boolean acceptAll = false;
	private static final String INT_VALUES_ARRAY_TAG = "values", FLUID_TAG = "fluid", INFO_TAG = "info";

	public ModFluidTank(FluidTankWrapper info, FluidStack fluid, int maxFill, int maxDrain, FluidStack[] extraAcceptedFluids){
		this.fluid   = fluid;
		this.maxDrain= maxDrain;
		this.maxFill = maxFill;
		this.info    = info;
		if(extraAcceptedFluids != null) {
			this.acceptedFluids = new FluidStack[extraAcceptedFluids.length + 1];
            System.arraycopy(extraAcceptedFluids, 0, this.acceptedFluids, 0, extraAcceptedFluids.length);
			this.acceptedFluids[this.acceptedFluids.length - 1] = fluid;
		}else{
			acceptAll = true;
		}
	}

	public ModFluidTank(FluidTankWrapper info, FluidStack fluid, int maxTransfer, FluidStack[] extraAcceptedFluids){ this(info, fluid, maxTransfer, maxTransfer, extraAcceptedFluids); }

	public ModFluidTank(FluidTankWrapper info, FluidStack fluid, FluidStack[] extraAcceptedFluids){ this(info, fluid, info.getCapacity()/EnumConversions.SECONDS_TO_TICKS.convertToInt(5) > 0 ? info.getCapacity()/EnumConversions.SECONDS_TO_TICKS.convertToInt(5) : 1, extraAcceptedFluids); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public void setInfo(FluidTankWrapper info){ this.info = info; }

	public FluidTankWrapper getInfoWrapper(){ return info; }

	public int getMaxDrain() { return maxDrain; }

	public void setMaxDrain(int maxDrain) { this.maxDrain = maxDrain; }

	public int getMaxFill() { return maxFill; }

	public void setMaxFill(int maxFill) { this.maxFill = maxFill; }

	public void setCapacity(int capacity) { this.info.setCapacity(capacity); }

	public void setFluidType(FluidStack newFluid){ this.fluid = new FluidStack(newFluid.getFluid(), fluid.amount); }

	public void setFluidAmount(int amount){ this.fluid.amount = amount; }

	public boolean canUseFluidRightNow(FluidStack fluid){
		if(fluid.isFluidEqual(this.fluid))return true;
		return this.canFillFluidType(fluid) && this.fluid.amount == 0;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . FluidTank/Properties">


    @Override
    public FluidStack getFluid() { return fluid; }

    @Override
    public int getFluidAmount() { return fluid.amount; }

    @Nullable
    @Override
    public FluidStack getContents() { return fluid; }

    @Override
    public int getCapacity() { return info.getCapacity(); }

    @Override
    public boolean canFill() { return true; }

    @Override
    public boolean canDrain() { return true; }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        if(this.acceptAll) return true;
        else for(FluidStack s : this.acceptedFluids) if(s.isFluidEqual(fluidStack))return true;
        return false;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        if(this.acceptAll) return true;
        else for(FluidStack f : this.acceptedFluids) if(f.isFluidEqual(fluidStack)) return true;
        return false;
    }

    @Override
    public FluidTankInfo getInfo() { return new FluidTankInfo(this); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . FluidHandler Methods">

	public boolean canFillBucket(){return this.fluid.amount >= 1000;}

	public boolean fillBucket(){
		if(canFillBucket()){
			this.fluid.amount -= 1000;
			return true;
		}else return false;
	}

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{
            new FluidTankProperties(this.fluid, this.getCapacity())
        };
    }

    @Override
	public int fill(FluidStack resource, boolean doFill) {
		int ret = Math.max(resource.amount, 0);
		if(ret > 0){
			if(resource.getFluid() != fluid.getFluid() && fluid.amount == 0) if (canUseLiquid(resource)) this.setFluidType(resource);
			else ret = 0;
			if(resource.getFluid() == fluid.getFluid()){
				ret = Math.min(ret, Math.min(maxFill, info.getMaxFill()));
				if(doFill){
					ret = info.addAmountStored(ret);
					fluid.amount += ret;
				}
			}
		}
		return ret;
	}

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
	    int ret = Math.max(0, resource.amount);
	    if(maxDrain > 0 && ret > 0){
	        ret = Math.min(Math.min(maxDrain, info.getMaxDrain()), ret);
	        if(doDrain){
	            ret = info.removeAmountStored(ret);
	            fluid.amount -= ret;
            }
        }
        FluidStack stack = fluid.copy();
	    stack.amount = ret;
        return stack;
    }

    @Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
	    FluidStack drain = fluid.copy();
	    drain.amount = maxDrain;
	    return drain(drain, doDrain);
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Serializing">
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setIntArray(INT_VALUES_ARRAY_TAG, new int[] {this.maxFill, this.maxDrain});
		tag.setTag(FLUID_TAG, fluid.writeToNBT(new NBTTagCompound()));
		tag.setTag(INFO_TAG, info.serializeNBT());
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
	    if(nbt.hasKey(INT_VALUES_ARRAY_TAG)) {
            int[] values = nbt.getIntArray(INT_VALUES_ARRAY_TAG);
            this.maxFill = values[0];
            this.maxDrain = values[1];
        }
		if(nbt.hasKey(FLUID_TAG)) this.fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(FLUID_TAG));
		if(nbt.hasKey(INFO_TAG )) this.info.deserializeNBT(nbt.getCompoundTag(INFO_TAG));
	}
//</editor-fold>

	private boolean canUseLiquid(FluidStack liquid){
		for(FluidStack s : this.acceptedFluids) if(s.isFluidEqual(liquid)) return true;
		return false;
	}

}
