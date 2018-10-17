package squedgy.lavasources.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import squedgy.lavasources.generic.gui.IGuiElement;
import squedgy.lavasources.gui.elements.ElementSlot;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;

import static squedgy.lavasources.helper.GuiLocation.GuiLocations.default_gui;

public abstract class ModGui extends GuiContainer {

	private final List<IGuiElement> ELEMENTS = new ArrayList<>();
	protected final GuiLocation BACKGROUND;

	public ModGui(Container inventorySlotsIn) { this(inventorySlotsIn, default_gui);}

	public ModGui(Container inventorySlotsIn, GuiLocation backgroundGui){
		super(inventorySlotsIn);
		if(inventorySlotsIn != null){
			for(Slot s : inventorySlotsIn.inventorySlots) addElement(new ElementSlot(this, s, null));
		}
		this.BACKGROUND = backgroundGui;
		this.xSize = BACKGROUND.width;
		this.ySize = BACKGROUND.height;
	}

	public final FontRenderer getFontRenderer(){ return fontRenderer; }

	protected void addElement(IGuiElement toAdd){ ELEMENTS.add(toAdd);}
	protected void removeElement(IGuiElement toRemove){ ELEMENTS.remove(toRemove); }
	protected void clearElements(){}
	public void clearButtons(){ buttonList.clear(); }
	public void addButton(GuiButton b, String ignored) { buttonList.add(b); }

	protected abstract void setElements();

	@Override
	protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawForegroundLayer(mouseX, mouseY);
		ELEMENTS.forEach(e -> e.drawGuiElementForeground(mouseX, mouseY));
	}
	protected abstract void drawForegroundLayer(int mouseX, int mouseY);

	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		mc.renderEngine.bindTexture(BACKGROUND.texture.location);
		drawTexturedModalRect(getHorizontalMargin(), getVerticalMargin(), 0, 0, BACKGROUND.width, BACKGROUND.height);
		drawBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		ELEMENTS.forEach((element) -> element.drawGuiElementBackground(mouseX, mouseY, partialTicks));

	}

	/**
	 * Extends background layer in case a gui has extra bits that aren't part of an element
	 * @param partialTicks
	 * @param mouseX
	 * @param mouseY
	 */
	protected abstract void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	public final int getHorizontalMargin() { return (width - xSize) / 2; }
	public final int getVerticalMargin() { return (height - ySize) / 2; }

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		ELEMENTS.clear();
		if(inventorySlots != null) inventorySlots.inventorySlots.forEach(e -> addElement(new ElementSlot(this, e, null)));
		setElements();
	}
}
