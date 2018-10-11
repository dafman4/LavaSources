package squedgy.lavasources.gui;

import com.google.common.collect.Queues;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.lwjgl.input.Keyboard;
import squedgy.lavasources.gui.elements.BookDisplayFull;
import squedgy.lavasources.gui.elements.BookDisplayPartial;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModResearch;

import java.io.IOException;
import java.util.Queue;

public class GuiGuideBook extends ModGui {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static BookDisplayFull display;
	private Queue<BookDisplayPartial> partialQueue = Queues.newLinkedBlockingQueue();

	public GuiGuideBook(InventoryPlayer player, IInventory inventory, ResearchTab tabOpen){
		super(new ContainerEmpty(), GuiLocation.book_base);
		if(display == null) display = new BookDisplayFull(this, inventory, tabOpen);
		else display.setTab(tabOpen);
	}

	public GuiGuideBook(InventoryPlayer player, IInventory inventory) { this(player, inventory, ModResearch.DEFAULT_TAB); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Mod Gui">

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		display.drawGuiElement(getHorizontalMargin(), getVerticalMargin());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == Keyboard.KEY_E){
		}else {
			super.keyTyped(typedChar, keyCode);
		}
	}

//</editor-fold>

}
