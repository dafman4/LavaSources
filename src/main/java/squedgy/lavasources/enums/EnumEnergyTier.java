
package squedgy.lavasources.enums;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.helper.EnumConversions;

// Author David
public enum EnumEnergyTier {
	BASIC(4000, 20, 10, 20),
	INTERMEDIATE(7500, 40, 10, 60),
	ADVANCED(15000, 100, 10, 100),
	MASTER(25000, 200, 10, 200);
	public final int CAPACITY, MAX_TRANSFER, REQUIRED, GENERATED;

	EnumEnergyTier(int capacity, int maxTransfer, int required, int generated){
        this.CAPACITY = capacity;
        this.MAX_TRANSFER = maxTransfer;
        this.REQUIRED = required;
        this.GENERATED = generated;
    }

	
	public int getRequired(){ return this.REQUIRED; }
	
	public int getGenerated(){ return this.GENERATED; }
	
	public ModEnergyStorage getEnergyStorage(int currentHeld){ return getEnergyStorage(this.CAPACITY, this.getGenerated(), this.getRequired(), currentHeld); }
	public ModEnergyStorage getEnergyStorage(int capacity, int maxReceived, int maxExtracted, int currentHeld){ return new ModEnergyStorage(true, true,capacity, maxReceived,maxExtracted, currentHeld); }
	public ModEnergyStorage getEnergyStorage(int capacity, int currentHeld){return getEnergyStorage(capacity, MAX_TRANSFER, MAX_TRANSFER, currentHeld);}

	public static EnumEnergyTier getTier(EnumUpgradeTier tier){
		return EnumEnergyTier.values()[tier.LEVEL];
	}
	
}
