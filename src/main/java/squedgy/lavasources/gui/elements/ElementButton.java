package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.FontRenderer;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.helper.GuiLocation.GuiLocations;

public abstract class ElementButton extends GuiElement{

	protected GuiLocation normalImage, hoverImage, disabledImage, borderVertical, borderHorizontal;
	protected boolean hovered, enabled = true, visible = true;
	protected String buttonText;

	public ElementButton(ModGui drawer, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled, GuiLocation borderHorizontal, GuiLocation borderVertical){
		super(drawer, x, y, normal.width, normal.height, null);
		this.normalImage = normal;
		this.hoverImage = hover;
		this.disabledImage = disabled;
		this.borderHorizontal = borderHorizontal;
		this.borderVertical = borderVertical;
		this.buttonText = buttonText;
	}

	public ElementButton(ModGui drawer, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled){
		this(drawer, x, y, buttonText, normal, hover, disabled, GuiLocations.book_border_horizontal, GuiLocations.book_border_vertical);
	}

	public ElementButton(ModGui drawer, int x, int y, String buttonText){
		this(drawer, x, y, buttonText, GuiLocations.default_button, GuiLocations.default_button_hover, GuiLocations.default_button_disabled);
	}

	protected GuiLocation getDrawLocation(){
		if(!this.enabled) return  (disabledImage);
		else if(this.hovered) return (hoverImage);
		else return (normalImage);
	}


	protected void drawButtonString(FontRenderer renderer){
		int j = 0xE0E0E0;
		if (!this.enabled) j = 0xA0A0A0;
		else if (this.hovered)j = 0xFFFFA0;
		drawer.drawCenteredString(renderer, this.buttonText, this.locationX + this.width / 2, this.locationY + (this.height - 8) / 2, j);
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) { if(hovered) displayToolTip(mouseX, mouseY); }

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks){
		checkHovered(mouseX, mouseY);
		GuiLocation draw = getDrawLocation();
		//draw border if possible
		int yDraws = 0, xDraws = 0;
		if(borderVertical != null) {
			bindTexture(borderVertical.texture.location);
			for (; yDraws < height / borderVertical.height; yDraws++) {
				drawTexturedModal(locationX, locationY + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, borderVertical.height);
				drawTexturedModal(locationX + width - borderVertical.width, locationY + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, borderVertical.height);
			}
			int drawHeight = height - borderVertical.height * yDraws;
			drawTexturedModal(locationX, locationY + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, drawHeight);
			drawTexturedModal(locationX + width - borderVertical.width, locationY + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, drawHeight);
		}
		if(borderHorizontal != null) {
			bindTexture(borderHorizontal.texture.location);
			for (; xDraws < height / borderHorizontal.height; xDraws++) {
				drawTexturedModal(locationX + borderHorizontal.width * xDraws, locationY, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
				drawTexturedModal(locationX + borderHorizontal.width * xDraws, locationY + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
			}
			int drawWidth = width - borderHorizontal.width * xDraws;
			drawTexturedModal(locationX + borderHorizontal.width * xDraws, locationY, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);
			drawTexturedModal(locationX + borderHorizontal.width * xDraws, locationY + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);
		}
		//draw background if this is null then we should indeed throw an error as the button should have textures
		bindTexture(draw.texture.location);
		for(xDraws = 0; xDraws < (width-2)/draw.width; xDraws++){
			for(yDraws = 0; yDraws < (height-2)/draw.height; yDraws++){
				drawTexturedModal(locationX + draw.width*xDraws, locationY + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, draw.height);
			}
			drawTexturedModal(locationX + draw.width*xDraws, locationY + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, height - draw.height*yDraws);
		}
		drawTexturedModal(locationX + draw.width*xDraws, locationY + draw.height * draw.height, draw.textureX, draw.textureY,width - draw.width*xDraws, height - draw.height*yDraws);

	}



	public abstract void displayToolTip(int mouseX, int mouseY);

	public void checkHovered(int mouseX, int mouseY){
		mouseX -= (drawer.getGuiLeft());
		mouseY -= (drawer.getGuiTop());
		hovered = (mouseX > locationX && mouseX < locationX + normalImage.width && mouseY > locationY && mouseY < locationY + normalImage.height);
	}

	public void mouseDragged(int mouseX, int mouseY){}
	public void mouseReleased(int mouseX, int mouseY){}
	public void mousePressed(int mouseX, int mouseY){}
	public boolean isMouseOver(){ return hovered; }
	public void mouseClickMove(int mouseX, int mouseY, int mouseButtonClicked, long timeSinceLastClick){}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		LavaSources.writeMessage(getClass(), "phase: "+ phase +", visible: " + visible);
		return visible && (ModGui.EnumDrawPhase.BUTTONS == phase || ModGui.EnumDrawPhase.FOREGROUND == phase);
	}
}
