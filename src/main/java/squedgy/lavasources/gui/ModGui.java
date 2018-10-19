package squedgy.lavasources.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import squedgy.lavasources.gui.elements.ElementSlot;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;

import static squedgy.lavasources.helper.GuiLocation.GuiLocations.default_gui;

public abstract class ModGui extends GuiContainer {
	protected final List<GuiElement> ELEMENTS = new ArrayList<>();
	protected final GuiLocation BACKGROUND;
	private float lastPartial;

	public ModGui(Container inventorySlotsIn) { this(inventorySlotsIn, default_gui);}

	public ModGui(Container inventorySlotsIn, GuiLocation backgroundGui){
		super(inventorySlotsIn);
		for(Slot s : inventorySlotsIn.inventorySlots) addElement(new ElementSlot(this, s, null));
		this.BACKGROUND = backgroundGui;
		this.xSize = BACKGROUND.width;
		this.ySize = BACKGROUND.height;
	}

	@Override
	public void initGui() {
		super.initGui();
		ELEMENTS.clear();
		setElements();
		ELEMENTS.forEach(GuiElement::init);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ELEMENTS.forEach(GuiElement::close);
	}

	public final FontRenderer getFontRenderer(){ return fontRenderer; }

	public boolean addElement(GuiElement toAdd){ return ELEMENTS.add(toAdd);}
	public void removeElement(GuiElement toRemove){ ELEMENTS.remove(toRemove); }
	protected void clearElements(){ ELEMENTS.clear(); }
	public void clearButtons(){ buttonList.clear(); }
	protected abstract void setElements();

	@Override
	protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if(ELEMENTS.stream().anyMatch(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_BACKGROUND)))
			ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_FOREGROUND)).forEach(e -> e.drawSecondGuiForeground(mouseX, mouseY));
		else{
			drawForegroundLayer(mouseX, mouseY);
			ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.FOREGROUND)).forEach(e -> e.drawGuiElementForeground(mouseX, mouseY));
		}
	}

	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(BACKGROUND.texture.location);
		drawTexturedModalRect(guiLeft, guiTop, BACKGROUND.textureX, BACKGROUND.textureY, BACKGROUND.width, BACKGROUND.height);
		drawBackgroundLayer(partialTicks, mouseX, mouseY);
		ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.BACKGROUND)).forEach(e -> e.drawGuiElementBackground(mouseX, mouseY, partialTicks));
		ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.BUTTONS)).forEach(e -> e.drawGuiElementBackground(mouseX, mouseY, partialTicks));
		if(ELEMENTS.stream().anyMatch(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_BACKGROUND)))
			ELEMENTS.stream().filter(e -> e.drawsOnPhase(EnumDrawPhase.SECONDARY_GUI_BACKGROUND)).forEach(e -> e.drawSecondGuiBackground(mouseX, mouseY, partialTicks));
	}

	protected abstract void drawForegroundLayer(int mouseX, int mouseY);
	protected abstract void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		ELEMENTS.clear();
		if(inventorySlots != null) inventorySlots.inventorySlots.forEach(e -> addElement(new ElementSlot(this, e, null)));
		setElements();
		ELEMENTS.forEach(e -> e.setDrawer(this));
	}

	public void drawTexturedModal(int xAddition, int yAddition, GuiLocation image){
		drawTexturedModalRect(
				guiLeft + xAddition,
				guiTop + yAddition,
				image.textureX,
				image.textureY,
				image.width,
				image.height
		);
	}

	public enum EnumDrawPhase{ BACKGROUND, BUTTONS, FOREGROUND, SECONDARY_GUI_BACKGROUND, SECONDARY_GUI_FOREGROUND}

}
