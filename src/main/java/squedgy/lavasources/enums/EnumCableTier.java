package squedgy.lavasources.enums;

import squedgy.lavasources.capabilities.ModEnergyStorage;

/**
 *
 * @author David
 */
public enum EnumCableTier {
	BASIC(1000, 400),
	INTERMEDIATE(2000, 800),
	ADVANCED(2750, 1000),
	MASTER(4000, 1200);
	
	public final int CAPACITY, MAX_RECEIVE, MAX_EXTRACT;

	EnumCableTier(int capacity, int maxTransfer){this(capacity, maxTransfer, maxTransfer);}

	EnumCableTier(int capacity, int maxReceive, int maxExtract){
		CAPACITY = capacity;
		MAX_RECEIVE = maxReceive;
		MAX_EXTRACT = maxExtract;
	}
	
	public ModEnergyStorage getEnergyStorage(){ return getEnergyStorage(0); }
	
	public ModEnergyStorage getEnergyStorage(int currentHeld){ return new ModEnergyStorage(true, true, CAPACITY, MAX_RECEIVE, MAX_EXTRACT, currentHeld); }
	
	public static EnumCableTier getCableTier(EnumUpgradeTier tier){ return EnumCableTier.values()[tier.LEVEL]; }

}
