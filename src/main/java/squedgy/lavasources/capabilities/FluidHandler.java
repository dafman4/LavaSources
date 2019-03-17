package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author David
 */
public class FluidHandler implements IFluidHandler, INBTSerializable<NBTTagCompound>{

//<editor-fold defaulstate="collapsed" desc=". . . . Fields/Constructors">
	private List<ModFluidTank> fluids = new ArrayList<>();
	private boolean sharedTank;
	private final CapacityAndStorageWrapper SHARED_TANK;

	public FluidHandler(boolean sharedTank, int capacity, int amountStored){
		this.sharedTank = sharedTank;
		this.SHARED_TANK = new CapacityAndStorageWrapper(1, 0);
	}

//</editor-fold>

//<editor-fold defaultstate=collapsed desc=". . . . Helpers">

	public void addFluidTank(ModFluidTank tank){
		if(sharedTank) tank.setInfo(SHARED_TANK);
		ModFluidTank check = getFluidTank(tank.getFluid());
		if(check == null) fluids.add(tank);
		else check.fill(tank.getFluid(), true);
	}

    public ModFluidTank getFluidTank(FluidStack fluid){
        for(ModFluidTank tank : this.fluids){
            if(tank.canUseFluidRightNow(fluid)) return tank;
        }
        return null;
    }

    public ModFluidTank getFluidTank(int index){ return index >= 0 && index < fluids.size() ? fluids.get(index) : null; }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Serialization">

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound(), fluids = new NBTTagCompound();
		for(int i = 0; i < this.fluids.size(); i++) fluids.setTag(i + "", this.fluids.get(i).serializeNBT());
		tag.setBoolean("exclusive", sharedTank);
		tag.setTag("fluids", fluids);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		fluids.clear();
		this.sharedTank = nbt.getBoolean("exclusive");
		NBTTagCompound fluidsStored = nbt.getCompoundTag("fluids");
		//I realize this isn't that efficient and get around to changing it when I get around to changing it
		for(String s: fluidsStored.getKeySet().stream().sorted().collect(Collectors.toList())){
			fluids.add(new ModFluidTank(new CapacityAndStorageWrapper(1000, 0),new FluidStack(FluidRegistry.WATER, 1000), ModFluidTank.ACCEPT_ALL_FLUIDS));
			fluids.get(fluids.size()-1).deserializeNBT(fluidsStored.getCompoundTag(s));
			if(sharedTank) fluids.get(fluids.size()-1).setInfo(this.SHARED_TANK);
		}
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Fluid Handler Methods">
	@Override
	public IFluidTankProperties[] getTankProperties() {
		IFluidTankProperties[] props = new IFluidTankProperties[this.fluids.size()];
		for(int i = 0; i < fluids.size(); i++) props[i] = fluids.get(i);
		return props;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		ModFluidTank tank = getFluidTank(resource);
		if(tank != null) return tank.fill(resource, doFill);
		return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		ModFluidTank tank = getFluidTank(resource);
		if(tank != null && tank.getFluid() != null && tank.getFluid().isFluidEqual(resource)) return tank.drain(resource.amount, doDrain);
		return new FluidStack(resource.getFluid(), 0);
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		ModFluidTank tank = getFluidTank(0);
		if(tank != null) return tank.drain(maxDrain, doDrain);
		return new FluidStack(FluidRegistry.WATER, 0);
	}

//</editor-fold>
}
