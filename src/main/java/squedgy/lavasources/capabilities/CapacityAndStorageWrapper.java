package squedgy.lavasources.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import squedgy.lavasources.LavaSources;

public class CapacityAndStorageWrapper implements INBTSerializable<NBTTagCompound> {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private int capacity, amountStored;
	private static final String CAPACITY_TAG = "capacity", STORED_TAG = "stored";

	public CapacityAndStorageWrapper(CapacityAndStorageWrapper a, CapacityAndStorageWrapper b){
		this(a.capacity + b.capacity, a.amountStored + b.amountStored);
		a = this;
		b = this;
	}

	public CapacityAndStorageWrapper(int capacity, int amountStored){
		this.capacity = capacity;
		this.amountStored = amountStored;
	}

	public CapacityAndStorageWrapper(int capacity){ this(capacity, 0); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public int getMaxDrain(){
		return amountStored;
	}

	public int getMaxFill(){return capacity - amountStored;}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		if(capacity < 1){
			LavaSources.writeMessage(getClass(), "capacity was attempted to be made lower than 1. this is NOT okay. Setting capacity to 1");
			capacity = 1;
		}
		this.capacity = capacity;
	}

	public int getAmountStored() {
		return amountStored;
	}

	public void setAmountStored(int amountStored) {
		if(amountStored < 0){
			LavaSources.writeMessage(getClass(), "Amount stored as attempted to be made less than 0. this is NOT okay. setting amount stored to 0");
			amountStored = 0;
		}
		this.amountStored = amountStored;
	}

	public int addAmountStored(int amountToAdd){
		int ret = Math.min(this.capacity - amountStored, amountToAdd);
		this.amountStored += ret;
		return ret;
	}

	public int removeAmountStored(int amountToRemove){
		int ret = Math.min(this.amountStored, amountToRemove);
		this.amountStored -= ret;
		return ret;
	}

	public int getSpaceAvailable(){ return capacity - amountStored; }

//</editor-fold>

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(CAPACITY_TAG, capacity);
		tag.setInteger(STORED_TAG, amountStored);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.amountStored = nbt.getInteger(STORED_TAG);
		this.capacity = nbt.getInteger(CAPACITY_TAG);
	}
}
