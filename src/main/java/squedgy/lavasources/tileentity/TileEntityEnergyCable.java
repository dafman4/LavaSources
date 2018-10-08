package squedgy.lavasources.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.enums.EnumCableTier;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.generic.ModTileEntity;

import static squedgy.lavasources.block.BlockEnergyCable.CHARGED;

/**
 *
 * @author David
 */
public class TileEntityEnergyCable extends ModTileEntity implements ITickable, IUpgradeable{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static final String ENERGY_TAG = "energy", TIER_TAG = "tier";
	private EnumUpgradeTier tier;
	private ModEnergyStorage energy;
	private boolean charged;
	private boolean[] receivedFrom = new boolean[EnumFacing.values().length];

	public TileEntityEnergyCable(EnumUpgradeTier tier, boolean charged){
		this.tier = tier;
		energy = EnumCableTier.getCableTier(tier).getEnergyStorage();
	}
	
	public TileEntityEnergyCable(boolean charged){
		this(EnumUpgradeTier.BASIC, charged);
	}
	
	public TileEntityEnergyCable(){
		this(false);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public EnumUpgradeTier getTier() {
		return tier;
	}

	public ModEnergyStorage getEnergy() {
		return energy;
	}
	
	public void setReceivedFrom(EnumFacing f){
		this.receivedFrom[f.ordinal()] = true;
	}

	public boolean isCharged(){ return charged; }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	@Override
	public void update() {
		boolean flag = charged, dirty = false;

		if(!world.isRemote) {
			if (energy.getEnergyStored() > 0) {
				if (!charged) {
					charged = true;
				}
				for (EnumFacing f : EnumFacing.values()) {
					if (energy.getPowerStored() == 0) break;
					if (!receivedFrom[f.ordinal()]) {
						TileEntity te = world.getTileEntity(pos.offset(f));
						if (te instanceof TileEntityEnergyCable) {
							((TileEntityEnergyCable) te).setReceivedFrom(f.getOpposite());
						}
						if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())) {
							transferEnergy(te, f);
							dirty = true;
						}
					}
				}
			}
			if (charged && energy.getEnergyStored() <= 0) {
				charged = false;
			}
			receivedFrom = new boolean[]{false, false, false, false, false, false};
		}
		if(flag != charged) notifyBlockUpdate();
		if(dirty) markDirty();
	}
	
	private void transferEnergy(TileEntity te, EnumFacing f){
		IEnergyStorage en = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
		if(en != null && en.canReceive()){
			int extracted = energy.extractEnergy(energy.getMaxExtracted(), true), received = en.receiveEnergy(extracted, true);
			received = en.receiveEnergy(energy.extractEnergy(Math.min(extracted,received), true), false);
			if(received > 0){
				energy.extractEnergy(received, false);
			}
		}
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity">

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY){
			return (T) energy;
		}
		return super.getCapability(capability, facing); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable">

	@Override
	public EnumUpgradeTier getCurrentTier() { return this.tier; }

	@Override
	public void setTier(EnumUpgradeTier tier) { this.tier = tier; }

	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound) {
		compound.setInteger(TIER_TAG, tier.LEVEL);
		compound.setTag(ENERGY_TAG, energy.serializeNBT());
		return compound;
	}

	@Override
	public void readItem(NBTTagCompound compound) {
		if(compound.hasKey(TIER_TAG))tier = EnumUpgradeTier.values()[compound.getInteger(TIER_TAG)];
		updateTierRelatedComponents();
		if(compound.hasKey(ENERGY_TAG))energy.deserializeNBT(compound.getCompoundTag(ENERGY_TAG));
	}
//</editor-fold>

	public void updateTierRelatedComponents(){
		updateTierRelatedComponents(energy.getEnergyStored());
	}

	private void updateTierRelatedComponents(int energyStored){
		energy = EnumCableTier.getCableTier(tier).getEnergyStorage(energyStored);
	}
	
}
