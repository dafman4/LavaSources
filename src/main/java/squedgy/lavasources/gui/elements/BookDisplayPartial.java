package squedgy.lavasources.gui.elements;

import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.ResearchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDisplayPartial extends ElementBookDisplay{

	public static final int WIDTH = 100, HEIGHT = 160, LOCATION_X = 41, LOCATION_Y = 0, N_S_PADDING = 5, E_W_PADDING = 5, ELEMENT_PADDING = 3;
	public static final GuiLocation background = GuiLocation.GuiLocations.book_partial;
	private final List<GuiElement> elements = new ArrayList<>();
	public static final PageButton
		NEXT_PAGE = new PageButton(0, TOP_LEFT_X + LOCATION_X, HEIGHT + LOCATION_Y + TOP_LEFT_Y, "", PageButton.EnumPageButtonType.NEXT_PAGE),
		PREVIOUS_PAGE = new PageButton(1, TOP_LEFT_X + LOCATION_X + WIDTH - PageButton.EnumPageButtonType.PREVIOUS_PAGE.GENERAL.width, HEIGHT + LOCATION_Y + TOP_LEFT_Y, "", PageButton.EnumPageButtonType.PREVIOUS_PAGE);
	private ResearchButton parent;

	public BookDisplayPartial(ModGui drawer, ResearchButton parent, GuiElement... components){
		super(drawer, LOCATION_X, LOCATION_Y, WIDTH, HEIGHT, null, parent.getDrawer());
		elements.addAll(Arrays.asList(components));
		this.parent = parent;
	}

	public static int getTotalDrawHeight(){ return HEIGHT; }
	public static int getTotalDrawWidth(){ return WIDTH; }
	public int getDrawHeight(){ return elements.stream().mapToInt(i -> i.height+3).sum(); }
	public int getElementsSize() { return elements.size(); }

	public boolean addElement(GuiElement element){
		LavaSources.writeMessage(getClass(), "tring to add " + element +
			"getDrawHeight = " + getDrawHeight() +
			"element Height = " + element.height +
			"HEIGHT = " + HEIGHT +
			"N_S_PADDING = " + N_S_PADDING
		);
		if(getDrawHeight() + element.height + N_S_PADDING*2 > HEIGHT)
			return false;
		elements.add(element);
		return true;
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		elements.forEach(e -> e.drawGuiElementForeground(mouseX, mouseY));
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		drawer.drawDefaultBackground();
		bindTexture(background);
		drawTexturedModal(0,0, background);
		elements.forEach(g -> g.drawGuiElementBackground(mouseX, mouseY, partialTicks));
		NEXT_PAGE.drawButtonBackground(mc, mouseX, mouseY, partialTicks);
		PREVIOUS_PAGE.drawButtonBackground(mc, mouseX, mouseY, partialTicks);
	}

	public boolean shouldNextPage(int mouseX, int mouseY){ return NEXT_PAGE.isMouseOver(); }

	public boolean shouldPreviousPage(int mouseX, int mouseY){ return PREVIOUS_PAGE.isMouseOver(); }

	@Override
	public String toString() {
		return "BookDisplayPartial{" +
				"elements=" + elements +
				", parent=" + parent +
				'}';
	}
}
