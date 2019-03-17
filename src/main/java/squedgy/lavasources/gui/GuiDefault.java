package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.inventory.ContainerEmpty;

public class GuiDefault extends ModGui {

	public GuiDefault(InventoryPlayer player, IInventory inventory){ super(new ContainerEmpty()); }

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

	@Override
	protected void setElements(){ }

}
