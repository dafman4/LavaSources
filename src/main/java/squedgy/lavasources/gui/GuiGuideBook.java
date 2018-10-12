package squedgy.lavasources.gui;

import com.google.common.collect.Queues;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.lwjgl.input.Keyboard;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.BookDisplayFull;
import squedgy.lavasources.gui.elements.BookDisplayPartial;
import squedgy.lavasources.gui.elements.ElementBookDisplay;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModResearch;
import squedgy.lavasources.research.ResearchButton;

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
		addElement(display);
	}

	public GuiGuideBook(InventoryPlayer player, IInventory inventory) { this(player, inventory, ModResearch.DEFAULT_TAB); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Mod Gui">


	@Override
	public void initGui() {
		super.initGui();
		LavaSources.writeMessage(getClass(),"display == "+ display + "\n\tTab = " + display.getTab());
		if(!display.getTab().isButtonsUpdated()) {
			display.getTab().getRelatedResearch().forEach(b -> {
				b.x = b.x + getHorizontalMargin() + ElementBookDisplay.TOP_LEFT_X;
				b.y = b.y + getVerticalMargin() + ElementBookDisplay.TOP_LEFT_Y;
			});
			display.getTab().updateButtons();
		}
		display.getTab().getRelatedResearch().forEach(b -> b.setDrawer(this));
		buttonList.addAll(display.getTab().getRelatedResearch());
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) { }

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == Keyboard.KEY_E){
			this.mc.player.closeScreen();
		}else {
			super.keyTyped(typedChar, keyCode);
		}
	}

//</editor-fold>

}
