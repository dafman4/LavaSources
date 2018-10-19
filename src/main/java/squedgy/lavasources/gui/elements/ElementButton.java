package squedgy.lavasources.gui.elements;

import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.helper.GuiLocation.GuiLocations;

public abstract class ElementButton extends GuiElement{

	protected GuiLocation normalImage, hoverImage, disabledImage, border;
	protected boolean hovered, enabled = true, visible = true;
	protected String buttonText;

	public ElementButton(ModGui drawer, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled, GuiLocation border){
		super(drawer, x, y, normal.width, normal.height, null);
		this.normalImage = normal;
		this.hoverImage = hover;
		this.disabledImage = disabled;
		this.border = border;
		this.buttonText = buttonText;
	}

	public ElementButton(ModGui drawer, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled){
		this(drawer, x, y, buttonText, normal, hover, disabled,null);
	}

	public ElementButton(ModGui drawer, int x, int y, String buttonText){
		this(drawer, x, y, buttonText, GuiLocations.default_button, GuiLocations.default_button_hover, GuiLocations.default_button_disabled);
	}

	protected GuiLocation getDrawLocation(){
		if(!this.enabled) return  (disabledImage);
		else if(this.hovered) return (hoverImage);
		else return (normalImage);
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) { if(hovered) displayToolTip(mouseX, mouseY); }

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks){
		checkHovered(mouseX, mouseY);
		//this should return the state of the image we want to draw
		GuiLocation draw = getDrawLocation();
		//draw border if possible
		if(border != null){
			bindTexture(border);
			drawRectUsingTessellator(border);
		}
		//draw the actual button if this is null then we should indeed throw an error as the button should have textures
		bindTexture(draw);
		drawRectUsingTessellator(1,1, draw);

	}



	public abstract void displayToolTip(int mouseX, int mouseY);

	public void checkHovered(int mouseX, int mouseY){
		mouseX -= (getGuiLeft());
		mouseY -= (getGuiTop());
		hovered = (mouseX > locationX && mouseX < locationX + normalImage.width && mouseY > locationY && mouseY < locationY + normalImage.height);
	}

	public void mouseDragged(int mouseX, int mouseY){}
	public void mouseReleased(int mouseX, int mouseY){}
	public void mousePressed(int mouseX, int mouseY){}
	public boolean isMouseOver(){ return hovered; }
	public void mouseClickMove(int mouseX, int mouseY, int mouseButtonClicked, long timeSinceLastClick){}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		return visible && (ModGui.EnumDrawPhase.BUTTONS == phase || ModGui.EnumDrawPhase.FOREGROUND == phase);
	}
}
