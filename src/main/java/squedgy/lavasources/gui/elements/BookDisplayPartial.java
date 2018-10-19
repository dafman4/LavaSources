package squedgy.lavasources.gui.elements;

import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.GuiGuideBook;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.inventory.ContainerEmpty;
import squedgy.lavasources.research.ResearchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDisplayPartial extends ElementSubDisplay{

	public static final GuiLocation background = GuiLocation.GuiLocations.book_partial;
	public static final int N_S_PADDING = 5, E_W_PADDING = 5, ELEMENT_PADDING = 3, GUI_TOP = ((GuiGuideBook.DISPLAY_HEIGHT + GuiGuideBook.BACKGROUND_Y*2)-(background.height))/2, GUI_LEFT = ((GuiGuideBook.DISPLAY_WIDTH + GuiGuideBook.BACKGROUND_X * 2) - background.width)/2;
	public static final ElementPageButton
			NEXT_PAGE = new ElementPageButton(null, GUI_LEFT + background.width - ElementPageButton.EnumPageButtonType.PREVIOUS_PAGE.GENERAL.width, background.height + GUI_TOP, "", ElementPageButton.EnumPageButtonType.NEXT_PAGE),
			PREVIOUS_PAGE = new ElementPageButton(null, GUI_LEFT,background.height + GUI_TOP, "", ElementPageButton.EnumPageButtonType.PREVIOUS_PAGE);
	private List<GuiElement> components = new ArrayList<>();

	public BookDisplayPartial(ModGui parent, GuiElement... components){
		super(parent, background);
		this.components.addAll(Arrays.asList(components));
	}

	@Override
	public void extraSetDrawers(ModGui drawer) {
		super.extraSetDrawers(drawer);
		NEXT_PAGE.setDrawer(drawer);
		PREVIOUS_PAGE.setDrawer(drawer);
		this.components.forEach(e -> e.setDrawer(drawer));
		this.ELEMENTS.forEach(e -> e.setDrawer(drawer));
	}

	public static int getTotalDrawHeight(){ return background.height - N_S_PADDING*2; }
	public static int getTotalDrawWidth(){ return background.width; }
	public int getDrawHeight(){ return components.stream().mapToInt(i -> i.height + ELEMENT_PADDING).sum(); }
	public int getElementsSize() { return components.size(); }

	@Override
	public boolean addElement(GuiElement element){
		if(getDrawHeight() + element.height > getTotalDrawHeight())
			return false;
		element.locationX += GUI_LEFT;
		element.locationY += GUI_TOP;
		return components.add(element);
	}

	public boolean shouldNextPage(int mouseX, int mouseY){ return NEXT_PAGE.isMouseOver(); }

	public boolean shouldPreviousPage(int mouseX, int mouseY){ return PREVIOUS_PAGE.isMouseOver(); }

	@Override
	protected void setElements() {
		for(GuiElement e: components) super.addElement(e);
		super.addElement(NEXT_PAGE);
		super.addElement(PREVIOUS_PAGE);
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		getDrawer().drawDefaultBackground();
		bindTexture(background.texture.location);
		drawTexturedModal(0,0, background);
		super.drawGuiElementBackground(mouseX, mouseY, partialTicks);
	}

}
