package squedgy.lavasources.gui.elements;

import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;


public abstract class ElementSubDisplay extends GuiElement {

	protected GuiLocation background;
	protected List<GuiElement> ELEMENTS = new ArrayList();
	protected int guiTop, guiLeft;

	public ElementSubDisplay(ModGui drawer, GuiLocation background){
		super(drawer, (drawer.getXSize() - background.width)/2, (drawer.getYSize() - background.height)/2, background.width, background.height, null);
		this.background = background;
	}

	@Override
	public void init() {
		ELEMENTS.clear();
		setElements();
	}

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		ELEMENTS.stream().filter(e -> e.drawsOnPhase(ModGui.EnumDrawPhase.FOREGROUND)).forEach(e -> e.drawGuiElementForeground(mouseX, mouseY));
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		//the order does matter here which is why they're separate calls
		ELEMENTS.stream().filter(e -> e.drawsOnPhase(ModGui.EnumDrawPhase.BACKGROUND)).forEach(e -> e.drawGuiElementBackground(mouseX, mouseY, partialTicks));
		ELEMENTS.stream().filter(e -> e.drawsOnPhase(ModGui.EnumDrawPhase.BUTTONS)).forEach(e -> e.drawGuiElementBackground(mouseX, mouseY, partialTicks));
	}

	public void removeElement(GuiElement toRemove){ ELEMENTS.remove(toRemove); }
	protected void clearElements(){ ELEMENTS.clear(); }
	protected abstract void setElements();
	public boolean addElement(GuiElement e){
		return ELEMENTS.add(e);
	}

	@Override
	public void extraSetDrawers(ModGui drawer) {
		ELEMENTS.clear();
		setElements();
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		return phase == ModGui.EnumDrawPhase.BACKGROUND || phase == ModGui.EnumDrawPhase.FOREGROUND || phase == ModGui.EnumDrawPhase.BUTTONS;
	}
}
