package squedgy.lavasources.gui.elements;

import net.minecraft.inventory.IInventory;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;

import java.util.List;

public class ElementTextDisplay extends GuiElement {

	private final List<String> lines;
	private final boolean textShadow;

	public ElementTextDisplay(ModGui drawer, int locationX, int locationY, int width, int height, IInventory container, List<String> lines) {
		this(drawer, locationX, locationY, width, height, container, lines, false);
	}

	public ElementTextDisplay(ModGui drawer, int locationX, int locationY, int width, int height, IInventory container, List<String> lines, boolean shadow){
		super(drawer, locationX, locationY, width, height, container);
		this.lines = lines;
		textShadow = shadow;
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		for (int i = 0; i < lines.size(); i++) {
			drawString( lines.get(i),0 , mc.fontRenderer.FONT_HEIGHT * i, textShadow);
		}
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) { }

	public List<String> getLines(){ return lines;}

	@Override
	public String toString() {
		return "ElementTextDisplay{" +
				"lines=" + lines +
				", locationX=" + locationX +
				", locationY=" + locationY +
				'}';
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		return phase == ModGui.EnumDrawPhase.FOREGROUND;
	}
}
