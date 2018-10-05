package squedgy.lavasources.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import squedgy.lavasources.generic.ModSlot;
import squedgy.lavasources.init.ModItems;

/**
 *
 * @author David
 */
public class SlotCoreModifierOutput extends ModSlot {

	public SlotCoreModifierOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

    @Override
    public ItemStack[] getAllowedItems() {
        return new ItemStack[0];
    }


}
