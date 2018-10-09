package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;

public class BookDisplayFull extends ElementBookDisplay {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	public static final int DISPLAY_WIDTH = 183, DISPLAY_HEIGHT = 163;
	private int fullWidth, fullHeight, drawX = 0, drawY = 0;

	public BookDisplayFull(Gui drawer, int width, int height, IInventory container) {
		super(drawer, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, container);
		if(width < DISPLAY_WIDTH) width = DISPLAY_WIDTH;
		if(height < DISPLAY_HEIGHT) height = DISPLAY_HEIGHT;
		this.fullHeight = height;
		this.fullWidth = width;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">


//</editor-fold>

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {

	}

}
