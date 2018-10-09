package squedgy.lavasources.gui.elements;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Gui;
import squedgy.lavasources.helper.GuiLocation;

import java.util.Arrays;
import java.util.List;

public class ElementDraggableScreen extends GuiElement {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private int totalHeight, totalWidth;
	private GuiLocation background = GuiLocation.DEFAULT_SCROLLABLE_BACKGROUND;
	private final GuiLocation referenceImage = null;
	private List<ElementDraggableScreen> children = Lists.newArrayList();

	public ElementDraggableScreen(Gui drawer, int locationX, int locationY, int height, int width, ElementDraggableScreen... children){
		super(drawer, locationX, locationY,height, width, null);
		this.children.addAll(Arrays.asList(children));
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters/Helpers">

//</editor-fold>

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {
		mc.renderEngine.bindTexture(background.location);
		for(int verticalDraw = 0 ; verticalDraw <= height/background.height; verticalDraw++){
			for(int horizontalDraw = 0 ; horizontalDraw <= width/background.width; horizontalDraw++){
				drawTexturedModal(horizontalMargin, verticalMargin, horizontalDraw * background.width, verticalDraw * background.height, background.textureX, background.textureY, background.width, background.height);
			}
		}
	}

}
