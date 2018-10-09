package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

public class GuiDefault extends ModGui {

	public GuiDefault(InventoryPlayer player, IInventory inventory){ super(new ContainerEmpty()); }

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">


//</editor-fold>
}
