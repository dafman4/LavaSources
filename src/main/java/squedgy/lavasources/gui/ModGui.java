package squedgy.lavasources.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import squedgy.lavasources.gui.elements.ElementSlot;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.GuiLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static squedgy.lavasources.helper.GuiLocation.default_gui;

public abstract class ModGui extends GuiContainer {

	private final List<GuiElement> ELEMENTS = new ArrayList<>();
	protected final GuiLocation BACKGROUND;

	public ModGui(Container inventorySlotsIn) { this(inventorySlotsIn, default_gui);}

	public ModGui(@Nullable Container inventorySlotsIn, GuiLocation backgroundGui){
		super(inventorySlotsIn);
		if(inventorySlotsIn != null){
			for(Slot s : inventorySlotsIn.inventorySlots) addElement(new ElementSlot(this, s, null));
		}
		this.BACKGROUND = backgroundGui;
		this.xSize = BACKGROUND.width;
		this.ySize = BACKGROUND.height;
	}

	protected void addElement(GuiElement toAdd){ ELEMENTS.add(toAdd);}

	@Override
	protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) { drawForegroundLayer(mouseX, mouseY); }
	protected abstract void drawForegroundLayer(int mouseX, int mouseY);

	@Override
	protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		mc.renderEngine.bindTexture(BACKGROUND.location);
		drawTexturedModalRect(getHorizontalMargin(), getVerticalMargin(), 0, 0, BACKGROUND.width, BACKGROUND.height);
		drawBackgroundLayer(partialTicks, mouseX, mouseY);

		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		ELEMENTS.forEach((element) -> element.drawGuiElement(getHorizontalMargin(), getVerticalMargin()));
	}

	/**
	 * Extends background layer in case a gui has extra bits that aren't part of an element
	 * @param partialTicks
	 * @param mouseX
	 * @param mouseY
	 */
	protected abstract void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY);

	protected final int getHorizontalMargin() { return (width - xSize) / 2; }
	protected final int getVerticalMargin() { return (height - ySize) / 2; }
}
