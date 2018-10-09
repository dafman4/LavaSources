package squedgy.lavasources.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import squedgy.lavasources.gui.GuiLiquefier;
import squedgy.lavasources.inventory.slots.SlotLiquefier;
import squedgy.lavasources.tileentity.TileEntityLiquefier.SlotEnum;

/**
 *
 * @author David
 */
public class ContainerLiquefier extends ModContainer{

	
	public ContainerLiquefier(InventoryPlayer playerInventory, IInventory liquefier){
		super(playerInventory,liquefier, 8, 50,
			new SlotLiquefier(liquefier, 0, 83, 15));
	}

    @Override
    public String getFieldName(int fieldId) { return GuiLiquefier.EnumFields.values()[fieldId].name(); }

    @Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = this.inventorySlots.get(index);
		ItemStack previous = ItemStack.EMPTY;
		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();
			if(index == SlotEnum.INPUT_SLOT.ordinal()){
				if(!mergeItemStack(current,1,37, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(current, previous);
			}else{
                if(current.isItemEqual(Items.REDSTONE.getDefaultInstance())){
                    if(!mergeItemStack(current,0,1,false)){
                        return ItemStack.EMPTY;
                    }
                }else if(current.isItemEqual(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK).getDefaultInstance())){
                    if(!mergeItemStack(current,0,1,false)){
                        return ItemStack.EMPTY;
                    }
                }else if(index >= 28 && index < 37){
					if(!mergeItemStack(current,1,28,false))
						return ItemStack.EMPTY;
				}else{
					if(!mergeItemStack(current,28,37,true))
						return ItemStack.EMPTY;
				}
			}
			if(current.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			
			if(current.getCount() == previous.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, current);
		}
		
		return previous;
	}

}
