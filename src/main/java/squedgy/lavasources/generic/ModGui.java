package squedgy.lavasources.generic;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import squedgy.lavasources.gui.elements.ElementSlot;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;

import static squedgy.lavasources.helper.GuiLocation.DEFAULT_GUI;

public abstract class ModGui extends GuiContainer {

	private final List<GuiElement> ELEMENTS = new ArrayList<>();
	protected final GuiLocation BACKGROUND;

	public ModGui(Container inventorySlotsIn) { this(inventorySlotsIn, DEFAULT_GUI);}

	public ModGui(Container inventorySlotsIn, GuiLocation backgroundGui){
		super(inventorySlotsIn);
		if(inventorySlotsIn instanceof ModContainer){
			for(Slot s : ((ModContainer) inventorySlotsIn).getGuiSlots()) addElement(new ElementSlot(this, s, null));
		}
		this.BACKGROUND = backgroundGui;
	}

	protected void addElement(GuiElement toAdd){ ELEMENTS.add(toAdd);}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawForegroundLayer(mouseX, mouseY);
	}
	protected abstract void drawForegroundLayer(int mouseX, int mouseY);

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		mc.renderEngine.bindTexture(BACKGROUND.location);
		drawTexturedModalRect(getHorizontalMargin(), getVerticalMargin(), 0, 0, BACKGROUND.width, BACKGROUND.height);
		drawBackgroundLayer(partialTicks, mouseX, mouseY);

		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		ELEMENTS.forEach((element) -> element.drawGuiElement(getHorizontalMargin(), getVerticalMargin()));
	}

	protected abstract void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY);
	protected abstract int getHorizontalMargin();
	protected abstract int getVerticalMargin();
}
