package squedgy.lavasources.generic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

@FunctionalInterface
public interface IContainerCreator {
	public abstract Container getContainer(InventoryPlayer playerInventory, EntityPlayer playerIn);
}
