package squedgy.lavasources.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.tileentities.IPersistentInventory;
import squedgy.lavasources.generic.tileentities.IUpgradeable;

/**
 *
 * @author David
 */
public class TileEntityLavaSource extends ModTileEntity implements ITickable, IUpgradeable, IPersistentInventory {
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields">
	private int lavaGenerated;
	private ModFluidTank fluids;
	private EnumUpgradeTier tier;
	private int requiredPower;
	private ModEnergyStorage energy;
	private boolean destroyedByCreative;
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	private TileEntityLavaSource(EnumUpgradeTier tier){
		this.tier =tier;
		this.updateTierRelatedComponents(0,0);
		markDirty();
	}
	
	public TileEntityLavaSource(){
		this(EnumUpgradeTier.BASIC);
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	
	@Override
	public void update() {
		if(fluids.fill(new FluidStack(FluidRegistry.LAVA, lavaGenerated), false) > 0){//check for not max lava contained
			int possible = energy.extractEnergy(this.requiredPower, true);
			if(possible >= this.requiredPower){
				energy.extractEnergy(this.requiredPower, false);
				int filled = fluids.fill(new FluidStack(FluidRegistry.LAVA, lavaGenerated), true);
				markDirty();
			}
		}
		
		if(fluids.getFluidAmount() > 0){
			for(EnumFacing testFacing : EnumFacing.values()){
				TileEntity test = getWorld().getTileEntity(getPos().offset(testFacing));
				if(test != null){
					IFluidHandler handler = test.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, testFacing.getOpposite());
					if(handler != null) {
                        for (IFluidTankProperties props : handler.getTankProperties()) {
                            if (props.canDrainFluidType(fluids.getFluid())){
                                if(handler.fill(fluids.drain(fluids.getMaxDrain(), false), false) > 0) {
                                    int filled = handler.fill(fluids.drain(fluids.getMaxDrain(), false), true);
                                    fluids.drain(filled, true);
                                    markDirty();
                                }
                            }
                        }
                    }
				}
				if(fluids.getFluidAmount() == 0) break;
			}
		}
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Persistent">

    @Override
    public boolean isDestroyedByCreative() {
        return destroyedByCreative;
    }

    @Override
    public void setDestroyedByCreative(boolean destroyedByCreative) {
        this.destroyedByCreative = destroyedByCreative;
    }

    @Override
    public NBTTagCompound writeItem(NBTTagCompound compound) {
        compound.setInteger("tier", tier.LEVEL);
        compound.setInteger("fluid_stored", fluids.getFluidAmount());
        compound.setInteger("energy_stored", energy.getPowerStored());
        return compound;
    }

	@Override
	public void readItem(NBTTagCompound compound) {
		this.tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		int energy = this.energy.getEnergyStored(), fluid = fluids.getFluidAmount();
		if(compound.hasKey("energy_stored")) energy = compound.getInteger("energy_stored");
		if(compound.hasKey("fluid_stored")) fluid =  compound.getInteger("fluid_stored");
		this.updateTierRelatedComponents(energy, fluid);
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity Overloads">

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return (T)energy;
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)fluids;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}
	
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public int getLavaGenerated() {
		return lavaGenerated;
	}

	public void setLavaGenerated(int lavaGenerated) {
		this.lavaGenerated = lavaGenerated;
		markDirty();
	}

	public int getRequiredPower() {
		return requiredPower;
	}

	public void setRequiredPower(int requiredPower) {
		this.requiredPower = requiredPower;
		markDirty();
	}

	public ModFluidTank getFluids(){
		return fluids;
	}
	
	public IEnergyStorage getEnergy(){
		return energy;
	}
	
	public EnumUpgradeTier getTier(){
		return tier;
	}

	public boolean shouldDropSpecial(){return this.fluids.getFluidAmount()> 0 || this.energy.getEnergyStored() > 0 || this.tier.LEVEL > 0;}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable Overrides">
	
	@Override
	public EnumUpgradeTier getCurrentTier(){ return this.tier; }

	@Override
	public void setTier(EnumUpgradeTier tier){this.tier = tier;}

	public void updateTierRelatedComponents(){ this.updateTierRelatedComponents(energy.getEnergyStored(), fluids.getFluidAmount()); }

	private void updateTierRelatedComponents(int energyHeld, int liquidHeld){
		this.setLavaGenerated(tier.getFluidTier().GENERATED);
		this.setRequiredPower(tier.getEnergyTier().getRequired());
		fluids = tier.getFluidTier().getFluidTank(new FluidStack(FluidRegistry.LAVA, liquidHeld), false);
		energy = tier.getEnergyTier().getEnergyStorage(energyHeld);
	}


//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Helpers">

	@Override
	public String toString(){
		return "TileEntityLavaSource { " + "energy =" + energy + ", fluids = " + fluids +", needed = " + this.requiredPower + ", generated = " + this.lavaGenerated + "}";
	}

//</editor-fold>
	
}