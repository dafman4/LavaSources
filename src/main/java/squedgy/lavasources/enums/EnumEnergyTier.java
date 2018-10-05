
package squedgy.lavasources.enums;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.helper.EnumConversions;

// Author David
public enum EnumEnergyTier {
	BASIC(10000, 200, 200, 400),
	INTERMEDIATE(40000, 500, 200, 600),
	ADVANCED(100000, 1000, 200, 1000),
	MASTER(250000, 2500, 200, 1200);
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
	
	public static EnumEnergyTier getTier(EnumUpgradeTier tier){
		return EnumEnergyTier.values()[tier.LEVEL];
	}
	
}
