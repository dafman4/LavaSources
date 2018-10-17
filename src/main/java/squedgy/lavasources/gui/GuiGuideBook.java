package squedgy.lavasources.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.lwjgl.input.Keyboard;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.BookDisplayFull;
import squedgy.lavasources.gui.elements.GuiButton;
import squedgy.lavasources.inventory.ContainerEmpty;
import squedgy.lavasources.research.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModResearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiGuideBook extends ModGui {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static BookDisplayFull display;
	private final List<GuiButton> bookButtons = new ArrayList<>();

	public GuiGuideBook(InventoryPlayer player, IInventory inventory, ResearchTab tabOpen){
		super(new ContainerEmpty(), GuiLocation.GuiLocations.book_base);
		if(display == null) display = new BookDisplayFull(this, null, tabOpen);
		else  display.setTab(tabOpen);
	}

	public GuiGuideBook(InventoryPlayer player, IInventory inventory) { this(player, inventory, ModResearch.DEFAULT_TAB); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Mod Gui">


	@Override
	public void initGui() {
		super.initGui();
		LavaSources.writeMessage(getClass(),
		"\nhorizontalMargin = " + getHorizontalMargin() +
				"\nverticalMargin = " + getVerticalMargin() +
				"\nheight = " + height +
				"\nwidth = " + width +
				"\nxSize = " + xSize +
				"\nySize = " + ySize
		);
		display.init();
	}
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		display.close();
	}

	@Override
	protected void setElements() {
		display.setDrawer(this);
		addElement(display);
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == Keyboard.KEY_E){
			if(display.getDisplayedButton() != null) display.setDisplayedButton(null);
			else mc.player.closeScreen();
		}else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if(state == 0 ) display.mouseReleased(mouseX, mouseY);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		display.setDrawer(this);
		display.updateButtons();
	}
//</editor-fold>

}
