package squedgy.lavasources.gui.elements;

import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public class ElementImage extends GuiElement{

	private final GuiLocation image;

	public ElementImage(ModGui drawer, int locationX, int locationY, GuiLocation image) {
		super(drawer, locationX, locationY, image.width, image.height, null);;
		this.image = image;
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		bindTexture(image);
		drawTexturedModal(0 ,0, image);
	}

	@Override
	public String toString() {
		return "ElementImage{" +
				"image=" + image +
				", locationX=" + locationX +
				", locationY=" + locationY +
				'}';
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		return phase == ModGui.EnumDrawPhase.BACKGROUND;
	}
}
