package squedgy.lavasources.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squedgy.lavasources.LavaSources;

import java.util.stream.IntStream;

/**
 *
 * @author David
 */
public abstract class ModContainer extends Container{
	
	public final IInventory TILE_ENTITY;
	public static final int PLAYER_INVENTORY_SIZE = 27;
	public static final int PLAYER_HOTBAR_SIZE = 9;
	protected final int[] FIELDS;

	public ModContainer(InventoryPlayer player, IInventory inventory, Slot... slots){ this(player, inventory, 8, 50, slots); }
	
	public ModContainer(InventoryPlayer playerInventory, IInventory tileInventory,int playerInvX, int playerInvY, Slot... slots){
		TILE_ENTITY = tileInventory;
		for(Slot slot : slots)
			this.addSlotToContainer(slot);
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, playerInvX + j * 18, playerInvY + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, playerInvX + k * 18, playerInvY + 58));
        }
		FIELDS = new int[TILE_ENTITY.getFieldCount()];
	}

	public abstract String getFieldName(int fieldId);

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		for(IContainerListener tested : this.listeners){
			for(int i = 0; i < FIELDS.length; i ++){
				if(FIELDS[i] != TILE_ENTITY.getField(i))
				    
					tested.sendWindowProperty(this, i, TILE_ENTITY.getField(i));
			}
		}
		for(int i = 0; i < FIELDS.length; i ++){
			FIELDS[i] = TILE_ENTITY.getField(i);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int value){ this.TILE_ENTITY.setField(id, value); }
	
	@Override
	public final boolean canInteractWith(EntityPlayer playerIn){ return this.TILE_ENTITY.isUsableByPlayer(playerIn); }
	
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendAllWindowProperties(this, TILE_ENTITY);
	}
	
	@Override
	public abstract ItemStack transferStackInSlot(EntityPlayer playerIn, int index);

	public Slot[] getGuiSlots(){ return inventorySlots.toArray(new Slot[0]); }
	

}
