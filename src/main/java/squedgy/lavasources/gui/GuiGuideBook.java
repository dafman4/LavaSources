package squedgy.lavasources.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.ElementButton;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.inventory.ContainerEmpty;
import squedgy.lavasources.research.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModResearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiGuideBook extends ModGui {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	public static final int DISPLAY_WIDTH = 180, DISPLAY_HEIGHT = 160, TILE_SIZE = 20, BACKGROUND_X = 8, BACKGROUND_Y = 8;
	private int fullWidth, fullHeight, currentMouseX, currentMouseY;
	public static int drawX = 0, drawY = 0;
	private ResearchTab tabOpen;
	private final List<ElementButton> bookButtons = new ArrayList<>();

	public GuiGuideBook(InventoryPlayer player, IInventory inventory, ResearchTab tabOpen){
		super(new ContainerEmpty(), GuiLocation.GuiLocations.book_base);
		this.tabOpen = tabOpen;
	}

	public GuiGuideBook(InventoryPlayer player, IInventory inventory) { this(player, inventory, ModResearch.DEFAULT_TAB); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Mod Gui">

	@Override
	protected void setElements() {
		for(GuiElement e : tabOpen.getRelatedResearch()){
			e.setDrawer(this);
			LavaSources.writeMessage(getClass(), "adding " + e + " the the ELEMENTS");
			addElement(e);
		}
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GuiLocation background = tabOpen.getTabBackground();
		mc.renderEngine.bindTexture(background.texture.location);
		int xDrawAmount = (xSize - BACKGROUND_X*2)/background.width, yDrawAmount = (ySize - BACKGROUND_Y*2)/background.height;
		int xDraws, yDraws;
		for(xDraws = 0 ; xDraws <= xDrawAmount; xDraws++){
			int drawWidth = Math.min(background.width, (xSize - BACKGROUND_X*2) - background.width * xDraws),
				drawHeight = Math.min(background.width, ySize - BACKGROUND_Y*2);//yDraws will be zero once the loop starts, and I want to set it for afterwards
			for(yDraws = 0 ; yDraws < yDrawAmount; yDraws++){
				drawTexturedModalRect(
						background.width * xDraws + BACKGROUND_X + guiLeft,
						background.height * yDraws + BACKGROUND_Y + guiTop,
						background.textureX,
						background.textureY,
						drawWidth,
						drawHeight
				);
				//this is so that we can keep using it for the final draw
				drawHeight = Math.min(background.height, (ySize - BACKGROUND_Y*2) - background.height * (yDraws+1));
			}
			drawTexturedModalRect(
					background.width * xDraws + BACKGROUND_X + guiLeft,
					background.height* yDraws + BACKGROUND_Y + guiTop,
					background.textureX,
					background.textureY,
					drawWidth,
					drawHeight);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		//if they pressed their inventory key AND a sub gui is open close the sub gui or it's sub gui etc.
		if(mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) && (ELEMENTS.stream().anyMatch(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_BACKGROUND)))){
			ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_BACKGROUND)).forEach(GuiElement::close);
		}else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(state == 0) ELEMENTS.stream().filter(e -> e instanceof ElementButton).map(e -> (ElementButton) e).forEach(e -> e.mouseReleased(mouseX, mouseY));
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(clickedMouseButton == 0){
			addToDrawXAndY(currentMouseX - (mouseX - guiLeft - BACKGROUND_X), currentMouseY - (mouseY - guiTop - BACKGROUND_Y));
		}
	}

	protected void setTab(ResearchTab tabOpen){
		if(tabOpen != null){
			this.tabOpen = tabOpen;
			setElements();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		//not convinced I need this for this specific instance
//		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 0){
			currentMouseX = mouseX;
			currentMouseY = mouseY;
		}
	}

	private void addToDrawXAndY(int addX, int addY){
		if(fullWidth > DISPLAY_WIDTH){
			int add = drawX + DISPLAY_WIDTH + addX;
			if(add > fullWidth) addX = drawX + DISPLAY_WIDTH + addX - fullWidth;
			else if (drawX + addX < 0) addX = -drawX;
			drawX += addX;
		}
		if(fullHeight > DISPLAY_HEIGHT){
			if(drawY + DISPLAY_HEIGHT + addY > fullHeight) addY = drawY + DISPLAY_HEIGHT + addY - fullHeight;
			else if(drawY + addY < 0) addY = -drawY;
			drawY += addY;
		}

	}

	//</editor-fold>

}
