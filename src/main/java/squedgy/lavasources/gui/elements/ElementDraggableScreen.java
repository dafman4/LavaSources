package squedgy.lavasources.gui.elements;

import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementDraggableScreen extends GuiElement {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private int totalHeight, totalWidth;
	private GuiLocation background = GuiLocation.GuiLocations.default_scrollable_background;
	private final GuiLocation referenceImage = null;
	private List<ElementDraggableScreen> children = new ArrayList();

	public ElementDraggableScreen(ModGui drawer, int locationX, int locationY, int height, int width, ElementDraggableScreen... children){
		super(drawer, locationX, locationY,height, width, null);
		this.children.addAll(Arrays.asList(children));
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters/Helpers">

//</editor-fold>

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		bindTexture(background);
		for(int verticalDraw = 0 ; verticalDraw <= height/background.height; verticalDraw++){
			for(int horizontalDraw = 0 ; horizontalDraw <= width/background.width; horizontalDraw++){
				drawTexturedModal(horizontalDraw * background.width, verticalDraw * background.height, background.textureX, background.textureY, background.width, background.height);
			}
		}
	}

}
