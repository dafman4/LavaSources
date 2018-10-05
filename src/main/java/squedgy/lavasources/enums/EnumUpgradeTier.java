
package squedgy.lavasources.enums;

// Author David

import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.IUpgradeable;

public enum EnumUpgradeTier {
	BASIC,
	INTERMEDIATE,
	ADVANCED,
	MASTER;
	public final int LEVEL;
	
	EnumUpgradeTier(){ this.LEVEL = ordinal(); }
	
	public EnumEnergyTier getEnergyTier(){ return EnumEnergyTier.getTier(this); }
	
	public EnumFluidTier getFluidTier(){ return EnumFluidTier.getTier(this); }
	
	public static boolean isUpgradeFor(IUpgradeable upgrading, EnumUpgradeTier tier){ return upgrading.getCurrentTier().LEVEL < tier.LEVEL; }

    public EnumUpgradeTier getUpgrade(){
        EnumUpgradeTier ret = this;
        try{ ret = EnumUpgradeTier.values()[LEVEL +1]; }catch(ArrayIndexOutOfBoundsException ignored){}
        return ret;
    }
	
	public static boolean upgrade(Object toBeUpgraded, EnumUpgradeTier tier){
		boolean ret = false;
		try{
			IUpgradeable test = (IUpgradeable) toBeUpgraded;
			ret = isUpgradeFor(test, tier);
		}catch(Exception ignored){ }
		return ret;
	}
}
