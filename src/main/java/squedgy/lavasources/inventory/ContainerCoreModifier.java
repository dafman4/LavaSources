package squedgy.lavasources.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import squedgy.lavasources.gui.GuiCoreModifier;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.slots.SlotCoreModifierInput;
import squedgy.lavasources.inventory.slots.SlotCoreModifierOutput;
import squedgy.lavasources.tileentity.TileEntityCoreModifier.SlotEnum;

/**
 *
 * @author David
 */
public class ContainerCoreModifier extends ModContainer{
	
	public ContainerCoreModifier(InventoryPlayer playerInventory, IInventory coreModifier){
		super(playerInventory,coreModifier, 8, 50,
			new SlotCoreModifierInput(coreModifier , 0, 56, 17),
			new SlotCoreModifierOutput(coreModifier , 1, 104, 17));
	}


    @Override
    public String getFieldName(int fieldId) {
        return GuiCoreModifier.EnumFields.values()[fieldId].name();
    }

    @Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		
		Slot transfer = this.inventorySlots.get(index);
		ItemStack previous = ItemStack.EMPTY;
		if(transfer != null && transfer.getHasStack()){
			ItemStack current = transfer.getStack();
			previous = current.copy();
			if(index == SlotEnum.INPUT_SLOT.ordinal()){
				if(!mergeItemStack(current, 2, 38, true)){
					return ItemStack.EMPTY;
				}
				transfer.onSlotChange(current, previous);
				this.TILE_ENTITY.markDirty();
			}else if(index == SlotEnum.OUTPUT_SLOT.ordinal()){
				if(!mergeItemStack(current, 2, 38, true)){
					return ItemStack.EMPTY;
				}
			}else{
				if(current.isItemEqual(ModItems.EMPTY_CORE.getDefaultInstance())){
					if(!mergeItemStack(current, 0, 1, false))
						return ItemStack.EMPTY;
				}else if(index >= 29 && index < 38){
					if(!mergeItemStack(current, 2, 29, false))
						return ItemStack.EMPTY;
				}else{
					if(!mergeItemStack(current, 29 ,38, true))
						return ItemStack.EMPTY;
				}
			}
			
			if(current.isEmpty())
				transfer.putStack(ItemStack.EMPTY);
			else
				transfer.onSlotChanged();
			
			if(current.getCount() == previous.getCount())
				return ItemStack.EMPTY;
			transfer.onTake(playerIn, current);
		}
		
		return previous;
	}
	
		

}
