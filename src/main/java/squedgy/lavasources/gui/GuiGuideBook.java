package squedgy.lavasources.gui;

import com.google.common.collect.Queues;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.lwjgl.input.Keyboard;
import squedgy.lavasources.gui.elements.BookDisplayFull;
import squedgy.lavasources.gui.elements.BookDisplayPartial;
import squedgy.lavasources.gui.elements.ElementBookDisplay;
import squedgy.lavasources.helper.GuiLocation;

import java.io.IOException;
import java.util.Queue;

public class GuiGuideBook extends ModGui {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private Queue<BookDisplayPartial> partialQueue = Queues.newLinkedBlockingQueue();
	private BookDisplayFull currentPage;

	public GuiGuideBook(InventoryPlayer player, IInventory inventory) {
		super(new ContainerEmpty(), GuiLocation.BOOK_BASE);
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Mod Gui">

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == Keyboard.KEY_E){

		}
		super.keyTyped(typedChar, keyCode);
	}

//</editor-fold>

}
