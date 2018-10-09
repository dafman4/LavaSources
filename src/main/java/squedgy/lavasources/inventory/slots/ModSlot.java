package squedgy.lavasources.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ModSlot extends Slot {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
    public ModSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }
//</editor-fold>


    @Override
    public boolean isItemValid(ItemStack stack) {
        for(ItemStack item : getAllowedItems()) if(item.isItemEqual(stack)) return true;
        return false;
    }

    public abstract ItemStack[] getAllowedItems();

    @Override
    public String toString(){
        return getClass().getSimpleName();
    }

}
