package squedgy.lavasources.generic;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;

public abstract class ModGui extends GuiContainer {

	public ModGui(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		drawForegroundLayer(mouseX, mouseY);
	}
	protected abstract void drawForegroundLayer(int mouseX, int mouseY);

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.drawDefaultBackground();
		GlStateManager.color(1.0f, 1.0f, 1.0f,1.0f);
		drawBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	protected abstract void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY);
}
