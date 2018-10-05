package squedgy.lavasources.inventory.slots;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import squedgy.lavasources.generic.ModSlot;

/**
 *
 * @author David
 */
public class SlotLiquefier extends ModSlot {


    public SlotLiquefier(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public ItemStack[] getAllowedItems() {
        return new ItemStack[] {
            Items.REDSTONE.getDefaultInstance(),
            Item.getItemFromBlock(Blocks.REDSTONE_BLOCK).getDefaultInstance()
        };
    }
}
