package squedgy.lavasources.generic;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

@FunctionalInterface
public interface IGuiCreator {
	public abstract Gui createGui(InventoryPlayer player, IInventory inventory);
}
