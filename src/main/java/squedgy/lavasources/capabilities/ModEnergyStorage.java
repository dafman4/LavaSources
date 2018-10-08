package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import squedgy.lavasources.LavaSources;

/**
 *
 * @author David
 */
public final class ModEnergyStorage implements IEnergyStorage, INBTSerializable<NBTTagCompound>{
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static final String MAX_RECEIVED_TAG = "max_received", MAX_EXTRACTED_TAG = "max_extracted", INFO_TAG = "info", RECEIVE_TAG = "receive", EXTRACT_TAG = "extract", POWER_STORED_TAG = "power_stored", CAPACITY_TAG = "capacity";
	public boolean receive = true, extract = true;
	private int maxReceived, maxExtracted, capacity, powerStored;

	public ModEnergyStorage(boolean receive, boolean extract, int capacity, int maxReceived, int maxExtracted, int powerStored){
		this.setCapacity(capacity);
		this.setMaxExtracted(maxExtracted);
		this.setMaxReceived(maxReceived);
		this.setPowerStored(powerStored);
		this.setReceive(receive);
		this.setExtract(extract);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int capacity, int maxReceived, int maxExtracted){
		this(receive, extract, capacity, maxReceived, maxExtracted, 0);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int capacity, int maxTransferred){
		this(receive, extract, capacity, maxTransferred, maxTransferred);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int capacity){
		//5 seconds to fully fill by default or at least 1 energy/tick
		this(receive, extract, capacity, capacity /100 > 0 ? capacity /100 : 1);
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . EnergyStorage">

	public int internalExtract(int maxExtract, boolean simulate){
		int ret = 0;
		if(powerStored > 0){
			ret = Math.min(maxExtract, powerStored);
			if(!simulate)powerStored -= ret;
		}
		return ret;
	}

	public int internalReceive(int maxReceive, boolean simulate){
		int ret =0;
		if(!this.isFull()){
			ret = Math.min(maxReceive, capacity - powerStored);
			if(!simulate) powerStored += ret;
		}
		return ret;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int ret = 0;
		if(!this.isFull() && canReceive()){
			ret = Math.min(maxReceive, Math.min(this.maxReceived, capacity - powerStored));
			if(!simulate)powerStored += ret;
		}
		
		return ret;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int ret = 0;
		
		if(powerStored > 0 && canExtract()){
			ret = Math.min(maxExtract, Math.min(this.maxExtracted, powerStored));
			if(!simulate) powerStored -= ret;
		}
		
		return ret;
	}

	@Override
	public int getEnergyStored() {
		return powerStored;
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}
	@Override
	public boolean canExtract() {
		return extract;
	}

	@Override
	public boolean canReceive() {
		return receive;
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Serializable">

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(MAX_EXTRACTED_TAG, maxExtracted);
		tag.setInteger(MAX_RECEIVED_TAG, maxReceived);
		tag.setBoolean(RECEIVE_TAG, receive);
		tag.setBoolean(EXTRACT_TAG, extract);
		tag.setInteger(POWER_STORED_TAG, powerStored);
		tag.setInteger(CAPACITY_TAG, capacity);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if(compound.hasKey(MAX_RECEIVED_TAG))setMaxReceived(compound.getInteger(MAX_RECEIVED_TAG));
		if(compound.hasKey(MAX_EXTRACTED_TAG))setMaxExtracted(compound.getInteger(MAX_EXTRACTED_TAG));
		if(compound.hasKey(CAPACITY_TAG))setPowerStored(compound.getInteger(CAPACITY_TAG));
		if(compound.hasKey(POWER_STORED_TAG))setPowerStored(compound.getInteger(POWER_STORED_TAG));
		if(compound.hasKey(RECEIVE_TAG))setReceive(compound.getBoolean(RECEIVE_TAG));
		if(compound.hasKey(EXTRACT_TAG))setExtract(compound.getBoolean(EXTRACT_TAG));
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public boolean isReceive() { return receive; }

	public void setReceive(boolean receive) { this.receive = receive; }

	public boolean isExtract() { return extract; }

	public void setExtract(boolean extract) { this.extract = extract; }

	public int getCapacity() { return capacity; }

	public void setCapacity(int capacity) {
		if(capacity <= 0) capacity = 1;
		this.capacity = capacity;
		if(powerStored > capacity) setPowerStored(capacity);
	}

	public int getPowerStored() { return powerStored; }

	public void setPowerStored(int powerStored) {
		if(powerStored > capacity) powerStored = capacity;
		this.powerStored = powerStored;
	}

	public int getMaxReceived() { return maxReceived; }

	public void setMaxReceived(int maxReceived) {
		if(maxReceived < 0) maxReceived = 1;
		this.maxReceived = maxReceived;
	}

	public int getMaxExtracted() { return maxExtracted; }

	public void setMaxExtracted(int maxExtracted) {
		if(maxExtracted < 0) maxExtracted = 1;
		this.maxExtracted = maxExtracted;
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Helpers">
	@Override
	public String toString() {
		return "ModEnergyStorage{" + "receive=" + receive + ", extract=" + extract + ", Capacity=" + capacity + ", powerStored=" + powerStored +  ", maxReceived=" + maxReceived + ", maxExtracted=" + maxExtracted + '}';
	}

	private boolean isFull(){ return capacity <= powerStored; }
//</editor-fold>

}
