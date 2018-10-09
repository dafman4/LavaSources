package squedgy.lavasources.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.generic.IUpgradeable;


/**
 *
 * @author David
 */
public class TileEntityEnergyGenerator extends ModTileEntity implements ITickable, IUpgradeable, IPersistentInventory {
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

    protected ModEnergyStorage energy;
	protected EnumFacing facing;
	protected EnumUpgradeTier tier;
	protected int generated;
	protected boolean destroyedByCreative;
	
	public TileEntityEnergyGenerator(EnumUpgradeTier tier){
		this.tier = tier;
		updateTierRelatedComponents(0);
		facing = EnumFacing.NORTH;
	}
	
	
	public TileEntityEnergyGenerator(){ this(EnumUpgradeTier.BASIC); }
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . ITickable/helper methods">
	@Override
	public void update() { 
		if(this.isPowered()){
			energy.receive = true;
			energy.receiveEnergy(generated, false);
			energy.receive = false;
		}
		for(EnumFacing testFacing : EnumFacing.values()){
			int pushable = Math.min(energy.getMaxExtracted(), energy.getEnergyStored());
			if(pushable == 0) break;
			TileEntity test = getWorld().getTileEntity(getPos().offset(testFacing));
			if(test != null){
				IEnergyStorage handler = test.getCapability(CapabilityEnergy.ENERGY, testFacing.getOpposite());
				if(handler != null && handler.canReceive()){
					handler.receiveEnergy(pushable, false);
				}
			}
		}
	}

	public boolean isPowered() {
		return getWorld().isBlockPowered(getPos());
	}

	public EnumFacing getFacing() {
		return this.facing;
	}
	
	public void setFacing(EnumFacing facing){
		this.facing = facing;
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity overrides">

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		return super.getCapability(capability, facing); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		return super.hasCapability(capability, facing);
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable Overrides">

	@Override
	public EnumUpgradeTier getCurrentTier() { return tier; }

	@Override
	public void setTier(EnumUpgradeTier tier){ this.tier = tier; }

	@Override
	public void updateTierRelatedComponents(){ updateTierRelatedComponents( energy.getEnergyStored() ); }

	private void updateTierRelatedComponents(int energyStored){
		this.energy = this.tier.getEnergyTier().getEnergyStorage(energyStored);
		energy.receive = false;
		this.generated = this.tier.getEnergyTier().GENERATED;
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Persistent Inventory">

    @Override
    public boolean shouldDropSpecial() { return this.energy.getEnergyStored() > 0 || this.tier.LEVEL > 0; }

    @Override
    public boolean isDestroyedByCreative() { return destroyedByCreative; }

    @Override
    public void setDestroyedByCreative(boolean destroyedByCreative) { this.destroyedByCreative = destroyedByCreative; }

    @Override
    public NBTTagCompound writeItem(NBTTagCompound compound) {
        compound.setInteger("tier", tier.LEVEL);
        compound.setInteger("stored", energy.getPowerStored());
        return compound;
    }

	@Override
	public void readItem(NBTTagCompound compound) {
		tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		int energy = compound.hasKey("stored") ? compound.getInteger("stored") :  this.energy.getEnergyStored();
		updateTierRelatedComponents(energy);
	}

//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Helpers">

//</editor-fold>
	
}
