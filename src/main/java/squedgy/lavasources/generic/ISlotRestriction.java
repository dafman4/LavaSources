package squedgy.lavasources.generic;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ISlotRestriction  {
	public abstract boolean isItemValidForSlot(ItemStack item);
}
