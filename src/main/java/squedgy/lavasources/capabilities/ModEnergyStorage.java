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
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields">
	public boolean receive = true, extract = true;
	private int maxPowerStored, powerStored, maxReceived, maxExtracted;
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	public ModEnergyStorage(boolean receive, boolean extract, int maxPowerStored, int maxReceived, int maxExtracted, int powerStored){
		this.setMaxPowerStored(maxPowerStored);
		this.setMaxExtracted(maxExtracted);
		this.setMaxReceived(maxReceived);
		this.setPowerStored(powerStored);
		this.setReceive(receive);
		this.setExtract(extract);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int maxPowerStored, int maxReceived, int maxExtracted){
		this(receive, extract, maxPowerStored, maxReceived, maxExtracted, 0);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int maxPowerStored, int maxTransferred){
		this(receive, extract, maxPowerStored, maxTransferred, maxTransferred);
	}
	
	public ModEnergyStorage(boolean receive, boolean extract, int maxPowerStored){
		//5 seconds to fully fill by default or at least 1 energy/tick
		this(receive, extract, maxPowerStored, maxPowerStored/100 > 0 ? maxPowerStored/100 : 1);
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . EnergyStorage">

	public int internalExtract(int maxExtract, boolean simulate){
		int ret = 0;
		if(powerStored > 0){
			ret = Math.min(maxExtract, this.powerStored);
			if(!simulate)this.powerStored -= ret;
		}
		return ret;
	}

	public int internalReceive(int maxReceive, boolean simulate){
		int ret =0;
		if(!this.isFull()){
			ret = Math.min(maxReceive, maxPowerStored - powerStored);
			if(!simulate) this.powerStored += ret;
		}
		return ret;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int ret = 0;
		if(!this.isFull() && canReceive()){
			ret = Math.min(maxReceive, Math.min(this.maxReceived, this.maxPowerStored - this.powerStored));
			if(!simulate)
				this.powerStored += ret;
		}
		
		return ret;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int ret = 0;
		
		if(powerStored > 0 && canExtract()){
			ret = Math.min(maxExtract, Math.min(this.maxExtracted, this.powerStored));
			if(!simulate){
				powerStored -= ret;
				
			}
		}
		
		return ret;
	}

	@Override
	public int getEnergyStored() {
		return powerStored;
	}

	@Override
	public int getMaxEnergyStored() {
		return maxPowerStored;
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
		tag.setIntArray("values", new int[] {this.powerStored, this.maxReceived, this.maxExtracted, this.maxPowerStored});
		tag.setBoolean("receive", receive);
		tag.setBoolean("extract", extract);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int[] values = nbt.getIntArray("values");
		this.powerStored = values[0];
		this.maxReceived = values[1];
		this.maxExtracted = values[2];
		this.maxPowerStored = values[3];
		this.receive = nbt.getBoolean("receive");
		this.extract = nbt.getBoolean("extract");
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public boolean isReceive() {
		return receive;
	}

	public void setReceive(boolean receive) {
		this.receive = receive;
	}

	public boolean isExtract() {
		return extract;
	}

	public void setExtract(boolean extract) {
		this.extract = extract;
	}

	public int getMaxPowerStored() {
		return maxPowerStored;
	}

	public void setMaxPowerStored(int maxPowerStored) {
		if(maxPowerStored < 1)
			throw new IllegalArgumentException(LavaSources.MOD_ID + ": there was an issue setting the max power stored on an instance ModEnergyStorage. maxPowerStored is less than 1");
		this.maxPowerStored = maxPowerStored;
	}

	public int getPowerStored() {
		return powerStored;
	}

	public void setPowerStored(int powerStored) {
		if(powerStored < 0 )
			throw new IllegalArgumentException(LavaSources.MOD_ID + ": there was an issue setting the power stored on an instance of ModEnergyStorage. power stored is less than 0");
		if(powerStored > maxPowerStored)
			powerStored = maxPowerStored;
		this.powerStored = powerStored;
	}

	public int getMaxReceived() {
		return maxReceived;
	}

	public void setMaxReceived(int maxReceived) {
		if(maxReceived < 0)
			throw new IllegalArgumentException(LavaSources.MOD_ID + ": there was an issue setting the max received on an instance of ModEnergyStorage. MaxReceived is less than 0");
		this.maxReceived = maxReceived;
	}

	public int getMaxExtracted() {
		return maxExtracted;
	}

	public void setMaxExtracted(int maxExtracted) {
		if(maxExtracted < 0)
			throw new IllegalArgumentException(LavaSources.MOD_ID + ": there was an issue setting the max extracted on an instance of ModEnergyStorage. MaxExtracted is less than 0");
		this.maxExtracted = maxExtracted;
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Helpers">
	@Override
	public String toString() {
		return "ModEnergyStorage{" + "receive=" + receive + ", extract=" + extract + ", maxPowerStored=" + maxPowerStored + ", powerStored=" + powerStored + ", maxReceived=" + maxReceived + ", maxExtracted=" + maxExtracted + '}';
	}

	private boolean isFull(){
		return this.maxPowerStored <= this.powerStored;
	}
//</editor-fold>

}
