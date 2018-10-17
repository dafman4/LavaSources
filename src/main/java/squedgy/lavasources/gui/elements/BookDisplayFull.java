package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModResearch;
import squedgy.lavasources.research.ResearchButton;
import squedgy.lavasources.research.ResearchTab;

public class BookDisplayFull extends ElementBookDisplay {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	public static final int DISPLAY_WIDTH = 183, DISPLAY_HEIGHT = 163;
	private int fullWidth, fullHeight, drawX = 0, drawY = 0;
	private ResearchTab displayTab;
	private ResearchButton displayedButton = null;

	public BookDisplayFull(ModGui drawer, IInventory container, ResearchTab tab){
		super(drawer, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, container);
		this.fullHeight = DISPLAY_HEIGHT;
		this.fullWidth = DISPLAY_WIDTH;
		LavaSources.writeMessage(getClass(),"tab = " + tab);
		this.displayTab = tab;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public void setDisplayedButton(ResearchButton button){
		this.displayedButton = button;
		if(displayedButton != null){
			BookDisplayPartial.PREVIOUS_PAGE.setDrawer(drawer);
			BookDisplayPartial.NEXT_PAGE.setDrawer(drawer);
			drawer.clearButtons();
			drawer.addButton(BookDisplayPartial.NEXT_PAGE, "");
			drawer.addButton(BookDisplayPartial.PREVIOUS_PAGE, "");
		}
	}

	public ResearchButton getDisplayedButton(){ return displayedButton; }

//</editor-fold>

	public void init(){
		updateButtons();
		displayTab.getRelatedResearch().forEach(b -> b.setDrawer(this));
		LavaSources.writeMessage(getClass(),
			"\nlocationX = " + locationX +
			"\nlocationY = " + locationY +
			"\nverticalMargin = " + getVerticalMargin() +
			"\nhorizontalMargin =" + getHorizontalMargin()
		);
	}

	public void updateButtons(){
		displayTab.getRelatedResearch().forEach(b -> {
			b.x = b.getSaveX() * 20 + getHorizontalMargin() + ElementBookDisplay.TOP_LEFT_X;
			b.y = b.getSaveY() * 20  + getVerticalMargin() + ElementBookDisplay.TOP_LEFT_Y;
		});
	}

	public void close(){
		displayedButton = null;
	}

	public int getHorizontalMargin(){ return drawer.getHorizontalMargin(); }
	public int getVerticalMargin(){ return drawer.getVerticalMargin(); }

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		if(displayedButton!=null) displayedButton.getPage().drawGuiElementForeground(mouseX, mouseY);
		else{
			displayTab.getRelatedResearch().stream().filter(GuiButton::isMouseOver).forEach(b -> b.displayToolTip(mc, mouseX, mouseY));
		}
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		GuiLocation background = displayTab.getTabBackground();
		bindTexture(background);
		int xDrawAmount = width/background.width, yDrawAmount = height/background.height;
		int drawX = xDrawAmount * background.width, drawY = yDrawAmount * background.height;
		for(int xDraws = 0 ; xDraws <= xDrawAmount; xDraws++){
			for(int yDraws = 0 ; yDraws < yDrawAmount; yDraws++){
				if(xDraws != xDrawAmount) drawTexturedModal(
						background.width * xDraws,
						background.height * yDraws,
						background
				);
				else drawTexturedModal(
						drawX,
						background.height * yDraws,
						background.textureX,
						background.textureY,
						width - drawX,
						background.height
				);
			}
			drawTexturedModal(
					background.width * xDraws,
					drawY,
					background.textureX,
					background.textureY,
					(xDraws != xDrawAmount) ? background.width : width - drawX,
					height-drawY );
		}
		displayTab.getRelatedResearch().forEach(b -> b.drawGuiElementBackground(mouseX, mouseY, partialTicks));
		if(displayedButton != null){
			displayedButton.getPage().drawGuiElementBackground(mouseX, mouseY, partialTicks);
		}
	}

	public void setTab(ResearchTab tab){
		if(tab == null) tab = ModResearch.DEFAULT_TAB;
		this.displayTab = tab;

	}

	public ResearchTab getTab(){
		return displayTab;
	}

	public void mouseReleased(int mouseX, int mouseY){
		displayTab.getRelatedResearch().stream().filter(ResearchButton::isMouseOver).findFirst().ifPresent(b -> b.mouseReleased(mouseX, mouseY));
		if(displayedButton != null){
			if(displayedButton.getPage().shouldNextPage(mouseX, mouseY)) displayedButton.nextPage();
			if(displayedButton.getPage().shouldPreviousPage(mouseX, mouseY)) displayedButton.previousPage();
		}
	}

}
