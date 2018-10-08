package squedgy.lavasources.enums;

import squedgy.lavasources.capabilities.ModEnergyStorage;

/**
 *
 * @author David
 */
public enum EnumCableTier {
	BASIC(1000, EnumEnergyTier.BASIC),
	INTERMEDIATE(2000, EnumEnergyTier.INTERMEDIATE),
	ADVANCED(2750, EnumEnergyTier.ADVANCED),
	MASTER(4000, EnumEnergyTier.MASTER);
	
	public final int CAPACITY;
	private final EnumEnergyTier TIER;

	EnumCableTier(int capacity, EnumEnergyTier tier){
		CAPACITY = capacity;
		TIER = tier;
	}

	
	public ModEnergyStorage getEnergyStorage(){ return getEnergyStorage(0); }
	
	public ModEnergyStorage getEnergyStorage(int currentHeld){ return TIER.getEnergyStorage(CAPACITY, currentHeld); }
	
	public static EnumCableTier getCableTier(EnumUpgradeTier tier){ return EnumCableTier.values()[tier.LEVEL]; }

}
