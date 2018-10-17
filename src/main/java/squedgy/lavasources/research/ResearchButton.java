package squedgy.lavasources.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.gui.elements.*;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class ResearchButton extends GuiButton {
	private static int id = 0;
	@ObjectHolder("lavasources:link_image_border")
	public static final GuiLocation link_image_border = GuiLocation.getGuiLocation("link_image_border");
	private Research research;
	private List<String> description;
	private String saveDescription;
	private GuiLocation drawImage;
	private final int saveX, saveY;
	private BookDisplayFull drawer;
	private final List<BookDisplayPartial> pagesRepresenting = new ArrayList<>();
	private List<Map.Entry<String,String>> tempElements;
	private int page = 0;


	public ResearchButton(int x, int y, Research research, String description, GuiLocation drawImage, List<Map.Entry<String,String>> entries){
		super(++id, x*(link_image_border.width+2), y*(link_image_border.height+2),"", link_image_border, link_image_border, link_image_border, link_image_border, link_image_border);
		int textWidth = width*12;
		this.saveDescription = description;
		this.drawImage = drawImage;
		this.research = research;
		saveX = x;
		saveY = y;
		tempElements = entries;
		LavaSources.writeMessage(getClass(), "instantiated with " + toString());
	}

	@Override
	protected void drawButtonBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		drawGuiElementBackground(mouseX, mouseY, partialTicks);
	}

	@Override
	public void displayToolTip(Minecraft mc, int mouseX, int mouseY) {
		if(description == null) description = mc.fontRenderer.listFormattedStringToWidth(saveDescription, width * 10);
		GuiUtils.drawHoveringText((description), mouseX - drawer.getHorizontalMargin(), mouseY - drawer.getVerticalMargin(), mc.currentScreen.width, mc.currentScreen.height, width*12, mc.fontRenderer);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		if(hovered) drawer.setDisplayedButton(this);
	}

	public static int getId() { return id; }

	public Research getResearch() { return research; }

	public List<String> getDescription() { return description; }

	public GuiLocation getDrawImage() { return drawImage; }

	public int getSaveX() { return saveX; }

	public int getSaveY() { return saveY; }

	public BookDisplayFull getDrawer() { return drawer; }

	public void setDrawer(BookDisplayFull drawer){
		this.drawer = drawer;
		if(this.pagesRepresenting.size() == 0 && tempElements !=null && tempElements.size()  != 0){
			BookDisplayPartial toAdd = new BookDisplayPartial(drawer.getDrawer(), this);
			for(Map.Entry<String,String> entry : tempElements){
				GuiElement e = null;

				if(entry.getKey().contains("text")) {
					String text = entry.getValue();
					if(text .startsWith("lavasources.pages")) text = I18n.format(text);
					e = newTextDisplay(toAdd, drawer.getDrawer().mc.fontRenderer.listFormattedStringToWidth(text, BookDisplayPartial.WIDTH - 10));
				}else if(entry.getKey().contains("image"))
					e = newImage(toAdd,  GuiLocation.getGuiLocation(entry.getValue()));

				if(e != null && !toAdd.addElement(e)){
					if(e instanceof ElementTextDisplay){
						int heightLeft = BookDisplayPartial.HEIGHT - toAdd.getDrawHeight() - BookDisplayPartial.N_S_PADDING*2, fontHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
						int draws = heightLeft / fontHeight ;
						List<String> text = ((ElementTextDisplay) e).getLines(), first = text.subList(0, draws),second = text.subList(draws, text.size());
						toAdd.addElement(newTextDisplay(toAdd, first));
						LavaSources.writeMessage(getClass(), "adding " + toAdd);
						pagesRepresenting.add(toAdd);
						toAdd = newPartial();
						LavaSources.writeMessage(getClass(), "new partial = " + toAdd);
						toAdd.addElement(newTextDisplay(toAdd, second));
					}else {
						LavaSources.writeMessage(getClass(), "adding " + toAdd);
						pagesRepresenting.add(toAdd);
						toAdd = newPartial();
						LavaSources.writeMessage(getClass(), "new partial = " + toAdd);
					}
				}
			}
			LavaSources.writeMessage(getClass(), "adding " + toAdd);
			pagesRepresenting.add(toAdd);
			tempElements = null;
		}
	}

	public BookDisplayPartial newPartial(){ return new BookDisplayPartial(drawer.getDrawer(), this); }

	public ElementTextDisplay newTextDisplay(BookDisplayPartial addTo, List<String> strings){
		FontRenderer r = drawer.getDrawer().mc.fontRenderer;
		return new ElementTextDisplay(drawer.getDrawer(), BookDisplayPartial.E_W_PADDING + BookDisplayPartial.LOCATION_X + BookDisplayPartial.TOP_LEFT_X, BookDisplayPartial.N_S_PADDING + BookDisplayPartial.TOP_LEFT_Y + BookDisplayPartial.LOCATION_Y + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), BookDisplayPartial.WIDTH - BookDisplayPartial.E_W_PADDING*2, strings.size() * r.FONT_HEIGHT, null, strings);
	}

	public ElementImage newImage(BookDisplayPartial addTo, GuiLocation image){
		return new ElementImage      (drawer.getDrawer(), BookDisplayPartial.E_W_PADDING + BookDisplayPartial.LOCATION_X + BookDisplayPartial.TOP_LEFT_X + ((BookDisplayPartial.WIDTH - (BookDisplayPartial.E_W_PADDING*2) - image.width))/2, BookDisplayPartial.N_S_PADDING + BookDisplayPartial.TOP_LEFT_Y + BookDisplayPartial.LOCATION_Y + addTo.getDrawHeight() + BookDisplayPartial.ELEMENT_PADDING * addTo.getElementsSize(), image);
	}


	public BookDisplayPartial nextPage(){
		++page;
		if(page >= pagesRepresenting.size()) page = 0;
		return getPage();
	}

	public BookDisplayPartial previousPage(){
		if(page == 0) page = pagesRepresenting.size();
		--page;
		return getPage();
	}

	public BookDisplayPartial getPage(){ return pagesRepresenting.get(this.page); }

	public boolean shouldNextPage(int mouseX, int mouseY){
		return getPage().shouldNextPage(mouseX, mouseY);
	}

	public boolean shouldPreviousPage(int mouseX, int mouseY){
		return getPage().shouldPreviousPage(mouseX, mouseY);
	}

	private void drawTexturedModalRect(int x, int y, GuiLocation image){
		drawTexturedModalRect(x, y, image, drawer.getDrawer());
	}

	private static void drawTexturedModalRect(int x, int y, GuiLocation image, ModGui drawer){
		drawer.mc.renderEngine.bindTexture(image.texture.location);
		drawTexturedModalRect(x, y, image.textureX, image.textureY, image.height, image.width, drawer);
	}

	private static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int height, int width, ModGui drawer){
		drawer.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}

	@Override
	public String toString() {
		return "ResearchButton{" +
				"research=" + research +
				", description='" + saveDescription + '\'' +
				", drawImage=" + drawImage +
				", saveX=" + saveX +
				", saveY=" + saveY +
				", tempElements" + tempElements +
				'}';
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {

	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		int drawWidth = link_image_border.width, drawHeight = link_image_border.height;
		hovered = x < mouseX && x + width > mouseX && y < mouseY && y + height > mouseY;
		drawTexturedModalRect(x + 1, y + 1, link_image_border);

		drawTexturedModalRect(x + 2, y + 2, drawImage);
	}
}
