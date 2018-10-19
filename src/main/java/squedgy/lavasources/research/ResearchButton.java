package squedgy.lavasources.research;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.GuiGuideBook;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.gui.elements.*;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ResearchButton extends ElementButton {
	//this is necessary as it won't have gotten updated (these are created from a registry before the final registry refresh
	public static final GuiLocation link_image_border = GuiLocation.getGuiLocation("link_image_border");
	public static final int X_TILE_DIFFERENCE = (GuiGuideBook.TILE_SIZE - link_image_border.width)/2, Y_TILE_DIFFERENCE = (GuiGuideBook.TILE_SIZE - link_image_border.height)/2;
	private Research research;
	private List<String> description;
	private final int saveX, saveY, drawDiffX, drawDiffY, locDiffX, locDiffY;
	private final List<BookDisplayPartial> pagesRepresenting = new ArrayList<>();
	private List<Map.Entry<String,String>> tempElements;
	private int page = 0;
	private boolean activeSecondary = false;

	public ResearchButton(ModGui drawer, int x, int y, Research research, String description, GuiLocation drawImage, GuiLocation disabledImage, List<Map.Entry<String,String>> entries){
		super(drawer, x*GuiGuideBook.TILE_SIZE+((GuiGuideBook.TILE_SIZE-link_image_border.width)/2), y*(GuiGuideBook.TILE_SIZE)+((GuiGuideBook.TILE_SIZE-link_image_border.height)/2), description, drawImage, drawImage, disabledImage, link_image_border);
		int textWidth = width*12;
		this.research = research;
		saveX = x;
		saveY = y;
		drawDiffX = (link_image_border.width - drawImage.width);
		drawDiffY = (link_image_border.height - drawImage.height);
		locDiffX = drawDiffX/2;
		locDiffY = drawDiffY/2;
		tempElements = entries;
	}

	public ResearchButton(ModGui drawer, int x, int y, Research research, String description, GuiLocation drawImage, List<Map.Entry<String,String>> entries){
		this(drawer, x, y, research, description, drawImage, drawImage, entries);
	}

	@Override
	public void displayToolTip(int mouseX, int mouseY) {
		if(description == null)
			description = mc.fontRenderer.listFormattedStringToWidth(buttonText, width * 10);
		if(enabled) GuiUtils.drawHoveringText((description), mouseX -getGuiLeft(), mouseY - getGuiTop(), mc.currentScreen.width, mc.currentScreen.height, width*12, mc.fontRenderer);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		checkHovered(mouseX, mouseY);
		if(visible && enabled) {
			if (hovered) {
				activeSecondary = true;
				pagesRepresenting.forEach(ElementSubDisplay::init);
			} else if (activeSecondary) {
				if (shouldNextPage(mouseX, mouseY)) nextPage();
				if (shouldPreviousPage(mouseX, mouseY)) previousPage();
			}
		}
	}

	public Research getResearch() { return research; }

	public List<String> getDescription() { return description; }

	@Override
	public void extraSetDrawers(ModGui drawer) { pagesRepresenting.forEach(p -> p.setDrawer(drawer)); }

	public int getSaveX() { return saveX; }

	public int getSaveY() { return saveY; }

	public void nextPage(){
		++page;
		if(page >= pagesRepresenting.size()) page = 0;
	}

	public void previousPage(){
		if(page == 0) page = pagesRepresenting.size();
		--page;
	}

	public BookDisplayPartial getPage(){ return pagesRepresenting.get(this.page); }

	public boolean shouldNextPage(int mouseX, int mouseY){ return getPage().shouldNextPage(mouseX, mouseY); }

	public boolean shouldPreviousPage(int mouseX, int mouseY){ return getPage().shouldPreviousPage(mouseX, mouseY); }

	@Override
	public String toString() {
		return "ResearchButton{" +
				"research=" + research +
				", drawImage=" + normalImage +
				", saveX=" + saveX +
				", saveY=" + saveY +
				", tempElements" + tempElements +
				'}';
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) { if(visible && hovered && enabled) displayToolTip(mouseX, mouseY); }


	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		//update location in-case of scroll
		locationX = saveX * GuiGuideBook.TILE_SIZE - GuiGuideBook.drawX + X_TILE_DIFFERENCE;
		locationY = saveY * GuiGuideBook.TILE_SIZE - GuiGuideBook.drawY + Y_TILE_DIFFERENCE;
		if(research != null) {
			visible = mc.player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null) &&
					mc.player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null).hasAllResearch(research.getDependencies());
			if(visible) enabled = mc.player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null).hasResearch(research);
		}
		checkHovered(mouseX, mouseY);
		LavaSources.writeMessage(getClass(), "visible = " + visible + "\nresearch = " + research);
		if(visible && locationX > -border.width && locationX < GuiGuideBook.DISPLAY_WIDTH && locationY > -border.height && locationY < GuiGuideBook.DISPLAY_HEIGHT) {
			mc.renderEngine.bindTexture(border.texture.location);
			int textX, textY, drawWidth, drawHeight, drawX ,drawY;
			if(locationX <= 0){
				drawX = 0;
				drawWidth = Math.min(border.width, border.width + locationX);
				textX = border.textureX + border.width - drawWidth;
			}else{
				drawX = locationX;
				drawWidth = Math.min(border.width, GuiGuideBook.DISPLAY_WIDTH - locationX);
				textX = border.textureX;
			}
			if(locationY <= 0){
				drawY = 0;
				drawHeight= Math.min(border.height, border.height + locationY);
				textY = border.textureY + border.height - drawHeight;
			}else{
				drawY = locationY;
				drawHeight= Math.min(border.height, GuiGuideBook.DISPLAY_HEIGHT - locationY);
				textY = border.textureY;
			}
			bindTexture(border);
			drawRectUsingTessellator(
				drawX + getGuiLeft(),
				drawY + getGuiTop(),
				drawWidth,
				drawHeight,
				((float)textX)/border.texture.width,
				((float)textX + drawWidth)/border.texture.width,
				((float)textY)/border.texture.height,
				((float)textY + drawHeight)/border.texture.height
			);
			if (drawHeight > locDiffX && drawWidth > locDiffY) {
				GuiLocation normalImage = getDrawLocation();
				if(locationX + drawWidth >= GuiGuideBook.DISPLAY_WIDTH){
					drawWidth -= locDiffX;
					drawX += locDiffX;
				}else if (locationX > -locDiffX) {
					drawWidth -= drawDiffY;
					drawX += locDiffX;
				}else{
					drawWidth -= locDiffX;
				}
				if(locationY + drawHeight >= GuiGuideBook.DISPLAY_HEIGHT){
					drawHeight -= locDiffY;
					drawY += locDiffY;
				}else if(locationY > -locDiffY){
					drawHeight -= drawDiffX;
					drawY += locDiffY;
				}else{
					drawHeight -= locDiffY;
				}
				textX = drawX <= 0 ? normalImage.textureX + normalImage.width - drawWidth : normalImage.textureX;
				textY = drawY <= 0 ? normalImage.textureY + normalImage.height- drawHeight: normalImage.textureY;
				bindTexture(normalImage);
				drawRectUsingTessellator(
					drawX + getGuiLeft(),
					drawY+ getGuiTop(),
					drawWidth,
					drawHeight,
					((float)textX)/normalImage.texture.width,
					((float)textX + drawWidth)/normalImage.texture.width,
					((float)textY)/normalImage.texture.height,
					((float)textY + drawHeight)/normalImage.texture.height

				);
				if(!enabled){
					drawGradientRect(drawX + getGuiLeft(), drawY + getGuiTop(), drawX + getGuiLeft() + drawWidth, drawY + getGuiTop() + drawHeight, 0x440f0f0f, 0x440f0f0f);
				}
			}
		}
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		if((phase == ModGui.EnumDrawPhase.SECONDARY_GUI_BACKGROUND || phase == ModGui.EnumDrawPhase.SECONDARY_GUI_FOREGROUND) && activeSecondary) return true;
		return super.drawsOnPhase(phase);
	}

	@Override
	public ElementSubDisplay getSecondGui(){ return getPage(); }

	@Override
	public void init() {
		if(this.pagesRepresenting.size() == 0 && tempElements !=null && tempElements.size()  != 0){
			setPartials();
		}
	}

	@Override
	public boolean close() {
		this.activeSecondary = !getPage().close();
		return activeSecondary;
	}

	public void setPartials(){
		BookDisplayPartial toAdd = newPartial();
		for(Map.Entry<String,String> entry : tempElements){
			GuiElement e = null;

			if(entry.getKey().contains("text")) {
				String text = entry.getValue();
				if(text .startsWith("lavasources.pages")) text = I18n.format(text);
				e = newTextDisplay(toAdd, mc.fontRenderer.listFormattedStringToWidth(text, BookDisplayPartial.background.width - BookDisplayPartial.E_W_PADDING*2));
			}else if(entry.getKey().contains("image"))
				e = newImage(toAdd,  GuiLocation.getGuiLocation(entry.getValue()));

			if(e != null && !toAdd.addElement(e)){
				if(e instanceof ElementTextDisplay){
					int heightLeft = BookDisplayPartial.getTotalDrawHeight() - toAdd.getDrawHeight(), fontHeight = mc.fontRenderer.FONT_HEIGHT;
					int draws = heightLeft / fontHeight ;
					List<String> text = ((ElementTextDisplay) e).getLines(), first = text.subList(0, draws),second = text.subList(draws, text.size());
					toAdd.addElement(newTextDisplay(toAdd, first));
					pagesRepresenting.add(toAdd);
					toAdd = newPartial();
					toAdd.addElement(newTextDisplay(toAdd, second));
				}else {
					pagesRepresenting.add(toAdd);
					toAdd = newPartial();
				}
			}
		}
		pagesRepresenting.add(toAdd);
		tempElements = null;
	}

	public BookDisplayPartial newPartial(){ return new BookDisplayPartial(getDrawer()); }

	public ElementTextDisplay newTextDisplay(BookDisplayPartial addTo, List<String> strings){
		FontRenderer r = mc.fontRenderer;
		return new ElementTextDisplay(getDrawer(), BookDisplayPartial.E_W_PADDING, BookDisplayPartial.N_S_PADDING + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), BookDisplayPartial.getTotalDrawWidth() - BookDisplayPartial.E_W_PADDING*2, strings.size() * r.FONT_HEIGHT, null, strings);
	}

	public ElementImage newImage(BookDisplayPartial addTo, GuiLocation image){
		return new ElementImage      (getDrawer(), BookDisplayPartial.E_W_PADDING + ((BookDisplayPartial.getTotalDrawWidth() - (BookDisplayPartial.E_W_PADDING*2) - image.width))/2, BookDisplayPartial.N_S_PADDING + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), image);
	}

	@Override
	public int getGuiLeft() {
		return super.getGuiLeft() + GuiGuideBook.BACKGROUND_X;
	}

	@Override
	public int getGuiTop() {
		return super.getGuiTop() + GuiGuideBook.BACKGROUND_Y;
	}
}
