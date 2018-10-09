package squedgy.lavasources.generic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

@FunctionalInterface
public interface IContainerCreator {
	public abstract Container getContainer(InventoryPlayer playerInventory, IInventory container);
}
