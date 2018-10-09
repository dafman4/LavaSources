package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.helper.GuiLocation;

public class ElementOverlay extends GuiElement{
	private final GuiLocation OVERLAY;

	public ElementOverlay(Gui drawer, int locationX, int locationY, IInventory container, GuiLocation overlay) {
		super(drawer, locationX, locationY, overlay.width, overlay.height, container);
		this.OVERLAY = overlay;
	}

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {
		mc.renderEngine.bindTexture(OVERLAY.location);
		drawTexturedModal(horizontalMargin,verticalMargin,0,0,OVERLAY.textureX, OVERLAY.textureY, OVERLAY.width, OVERLAY.height);
	}
}
