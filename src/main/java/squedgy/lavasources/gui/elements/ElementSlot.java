package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import squedgy.lavasources.helper.GuiLocation;

public class ElementSlot extends GuiElement {


//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final GuiLocation TEXTURE = GuiLocation.INVENTORY_SLOT;

	public ElementSlot(Gui parent, Slot s, IInventory container){
		this(parent, s.xPos-1, s.yPos-1, TEXTURE.width, TEXTURE.height, container);
	}

	private ElementSlot(Gui parent, int locationX, int locationY, int width, int height, IInventory container) {
		super(parent, locationX, locationY, width, height, container);
	}

//</editor-fold>

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {
		mc.renderEngine.bindTexture(TEXTURE.location);
		drawTexturedModal(horizontalMargin, verticalMargin, 0, 0, TEXTURE.textureX, TEXTURE.textureY, TEXTURE.width, TEXTURE.height);
	}

}
