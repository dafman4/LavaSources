
package squedgy.lavasources.generic;

// Author David

import squedgy.lavasources.enums.EnumUpgradeTier;

public interface IUpgradeable {
	public default boolean upgrade(EnumUpgradeTier tier){
		boolean ret = false;
		if(EnumUpgradeTier.isUpgradeFor(this, tier)){
			setTier(getCurrentTier().getUpgrade());
			updateTierRelatedComponents();
			ret = true;
		}
		return ret;
	}
	public abstract void updateTierRelatedComponents();
	public abstract EnumUpgradeTier getCurrentTier();
	public abstract void setTier(EnumUpgradeTier tier);
}
