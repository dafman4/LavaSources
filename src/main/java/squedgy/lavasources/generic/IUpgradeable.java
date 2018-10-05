
package squedgy.lavasources.generic;

// Author David

import squedgy.lavasources.enums.EnumUpgradeTier;

public interface IUpgradeable {
	public abstract boolean upgrade(EnumUpgradeTier tier);
	public abstract EnumUpgradeTier getCurrentTier();
}
