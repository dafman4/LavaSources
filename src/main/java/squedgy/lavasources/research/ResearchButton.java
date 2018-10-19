package squedgy.lavasources.research;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.GuiGuideBook;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.gui.elements.*;
import squedgy.lavasources.helper.GuiLocation;

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
	private final int saveX, saveY, drawDiffX, drawDiffY;
	private final List<BookDisplayPartial> pagesRepresenting = new ArrayList<>();
	private List<Map.Entry<String,String>> tempElements;
	private int page = 0;
	private boolean activeSecondary = false;


	public ResearchButton(ModGui drawer, int x, int y, Research research, String description, GuiLocation drawImage, List<Map.Entry<String,String>> entries){
		super(drawer, x*GuiGuideBook.TILE_SIZE+((GuiGuideBook.TILE_SIZE-link_image_border.width)/2), y*(GuiGuideBook.TILE_SIZE)+((GuiGuideBook.TILE_SIZE-link_image_border.height)/2),description, drawImage, drawImage, drawImage, link_image_border, link_image_border);
		int textWidth = width*12;
		this.research = research;
		saveX = x;
		saveY = y;
		drawDiffX = (link_image_border.width - drawImage.width);
		drawDiffY = (link_image_border.height - drawImage.height);
		tempElements = entries;
	}

	@Override
	public void displayToolTip(int mouseX, int mouseY) {
		if(description == null)
			description = mc.fontRenderer.listFormattedStringToWidth(buttonText, width * 10);
		GuiUtils.drawHoveringText((description), mouseX - drawer.getGuiLeft(), mouseY - drawer.getGuiTop(), mc.currentScreen.width, mc.currentScreen.height, width*12, mc.fontRenderer);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		checkHovered(mouseX, mouseY);
		if(hovered){
			activeSecondary = true;
			pagesRepresenting.forEach(ElementSubDisplay::init);
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
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		if(hovered) displayToolTip(mouseX, mouseY);
	}


	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		LavaSources.writeMessage(getClass(), "x: " + locationX + ", y: " + locationY);
		//update location in-case of scroll
		locationX = saveX * GuiGuideBook.TILE_SIZE - GuiGuideBook.drawX + X_TILE_DIFFERENCE + GuiGuideBook.BACKGROUND_X;
		locationY = saveY * GuiGuideBook.TILE_SIZE - GuiGuideBook.drawY + Y_TILE_DIFFERENCE + GuiGuideBook.BACKGROUND_Y;
		checkHovered(mouseX, mouseY);
		if(locationX + width > 0 && locationX < GuiGuideBook.DISPLAY_WIDTH && locationY + height > 0 && locationY < GuiGuideBook.DISPLAY_HEIGHT) {
			mc.renderEngine.bindTexture(link_image_border.texture.location);
			int textX, textY, drawWidth, drawHeight;
			if (locationX >= 0) {
				drawWidth = Math.min(link_image_border.width, GuiGuideBook.DISPLAY_WIDTH - locationX);
				textX = link_image_border.textureX;
			} else {
				drawWidth = locationX + link_image_border.width;
				textX = link_image_border.textureX + link_image_border.width - drawWidth;
			}
			if (locationY >= 0) {
				drawHeight = Math.min(link_image_border.height, GuiGuideBook.DISPLAY_HEIGHT - locationY);
				textY = link_image_border.textureY;
			} else {
				drawHeight = locationY + link_image_border.height;
				textY = link_image_border.textureY + link_image_border.height - drawHeight;
			}
			bindTexture(link_image_border);
			drawTexturedModal(link_image_border.textureX - textX, link_image_border.textureY - textY, textX, textY, drawWidth, drawHeight);
			if (drawHeight > drawDiffY && drawWidth > drawDiffX) {
				if (locationX >= 0) textX = normalImage.textureX;
				else textX = normalImage.textureX + normalImage.width - drawWidth + drawDiffX;
				if (locationY >= 0) textY = normalImage.textureY;
				else textY = normalImage.textureY + normalImage.height - drawHeight + drawDiffY;
				bindTexture(normalImage);
				drawTexturedModal(normalImage.textureX - textX + 1, normalImage.textureY - textY + 1, textX, textY, drawWidth - drawDiffX, drawHeight - drawDiffY);
			}
		}
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

	public BookDisplayPartial newPartial(){ return new BookDisplayPartial(drawer); }

	public ElementTextDisplay newTextDisplay(BookDisplayPartial addTo, List<String> strings){
		FontRenderer r = mc.fontRenderer;
		return new ElementTextDisplay(drawer, BookDisplayPartial.E_W_PADDING, BookDisplayPartial.N_S_PADDING + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), BookDisplayPartial.getTotalDrawWidth() - BookDisplayPartial.E_W_PADDING*2, strings.size() * r.FONT_HEIGHT, null, strings);
	}

	public ElementImage newImage(BookDisplayPartial addTo, GuiLocation image){
		return new ElementImage      (drawer, BookDisplayPartial.E_W_PADDING + ((BookDisplayPartial.getTotalDrawWidth() - (BookDisplayPartial.E_W_PADDING*2) - image.width))/2, BookDisplayPartial.N_S_PADDING + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), image);
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		if(phase == ModGui.EnumDrawPhase.SECONDARY_GUI_BACKGROUND && activeSecondary) return true;
		return super.drawsOnPhase(phase);
	}

	@Override
	public ElementSubDisplay getSecondGui(){
		return getPage();
	}

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
}
