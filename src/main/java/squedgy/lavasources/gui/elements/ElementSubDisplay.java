package squedgy.lavasources.gui.elements;

import squedgy.lavasources.LavaSources;
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

	public void removeElement(GuiElement toRemove){ ELEMENTS.remove(toRemove); }
	protected void clearElements(){ ELEMENTS.clear(); }
	protected abstract void setElements();
	public boolean addElement(GuiElement e){
		LavaSources.writeMessage(getClass(), "adding " + e);
		return ELEMENTS.add(e);
	}

	@Override
	public void extraSetDrawers(ModGui drawer) {
		ELEMENTS.clear();
		setElements();
	}
}
