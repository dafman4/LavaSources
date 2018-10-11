package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.IInventory;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

public class BookDisplayFull extends ElementBookDisplay {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	public static final int DISPLAY_WIDTH = 183, DISPLAY_HEIGHT = 163;
	private int fullWidth, fullHeight, drawX = 0, drawY = 0;
	private GuiLocation background;

	public BookDisplayFull(Gui drawer, IInventory container, ResearchTab tab){
		super(drawer, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, container);
		this.fullHeight = DISPLAY_HEIGHT;
		this.fullWidth = DISPLAY_WIDTH;
		LavaSources.writeMessage(getClass(), "background");
		this.background = tab.getTabBackground();
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">


//</editor-fold>

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {
		int xDrawAmount = width/background.width, yDrawAmount = height/background.height;
		int drawX = xDrawAmount * background.width, drawY =yDrawAmount * background.height;
		for(int xDraws = 0 ; xDraws <= xDrawAmount; xDraws++){
			for(int yDraws = 0 ; yDraws < yDrawAmount; yDraws++){
				if(xDraws != xDrawAmount) drawTexturedModal(horizontalMargin,verticalMargin,
						background.width * xDraws,
						background.height * yDraws,
						background
				);
				else drawTexturedModal(horizontalMargin, verticalMargin,
						drawX,
						background.height * yDraws,
						background.textureX,
						background.textureY,
						width - drawX,
						background.height
				);
			}
			drawTexturedModal(horizontalMargin, verticalMargin,
					background.width * xDraws,
					drawY,
					background.textureX,
					background.textureY,
					(xDraws != xDrawAmount) ? background.width : width - drawX,
					height-drawY );
		}
	}

	public void setTab(ResearchTab tab){

	}

}
