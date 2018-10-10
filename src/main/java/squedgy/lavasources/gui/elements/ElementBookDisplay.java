package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.helper.GuiLocation;

public abstract class ElementBookDisplay extends GuiElement {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	//Relative to the book's top left, this is how far down and over the top of draw space is
	public static final int TOP_LEFT_X = 8, TOP_LEFT_Y = 8;
	private ElementBookDisplay parent;

	public ElementBookDisplay(Gui drawer, int locationX, int locationY, int width, int height, IInventory container) {
		this(drawer, locationX, locationY, width, height, container, null);
	}

	public ElementBookDisplay(Gui drawer, int locationX, int locationY, int width, int height, IInventory container, ElementBookDisplay parent) {
		super(drawer, locationX + TOP_LEFT_X, locationY + TOP_LEFT_Y, width, height, container);
		this.parent = parent;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public ElementBookDisplay getParent(){ return parent; }
	public void setParent(ElementBookDisplay parent){ this.parent = parent; }

//</editor-fold>


	protected void  drawTexturedModal(int horizontalMargin, int verticalMargin, int xAddition, int yAddition, GuiLocation texture){
		mc.renderEngine.bindTexture(texture.location);
		drawTexturedModal(horizontalMargin, verticalMargin, xAddition, yAddition, texture.textureX, texture.textureY, texture.width, texture.height);
	}

	@Override
	protected void drawTexturedModal(int horizontalMargin, int verticalMargin, int xAddition, int yAddition, int textX, int textY, int width, int height) {
		super.drawTexturedModal(horizontalMargin, verticalMargin, xAddition, yAddition, textX, textY, width, height);
	}

	@Override
	protected void drawTexturedModal(int horizontalMargin, int verticalMargin, int xAddition, int yAddition, TextureAtlasSprite sprite, int width, int height) {
		super.drawTexturedModal(horizontalMargin, verticalMargin, xAddition, yAddition, sprite, width, height);
	}
}
