package squedgy.lavasources.gui.elements;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public class ElementSlot extends GuiElement {


//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final GuiLocation TEXTURE = GuiLocation.GuiLocations.inventory_slot;

	public ElementSlot(ModGui parent, Slot s, IInventory container){
		this(parent, s.xPos-1, s.yPos-1, TEXTURE.width, TEXTURE.height, container);
	}

	private ElementSlot(ModGui parent, int locationX, int locationY, int width, int height, IInventory container) {
		super(parent, locationX, locationY, width, height, container);
	}

//</editor-fold>

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		bindTexture(TEXTURE);
		drawTexturedModal(0, 0, TEXTURE.textureX, TEXTURE.textureY, TEXTURE.width, TEXTURE.height);
	}

}
