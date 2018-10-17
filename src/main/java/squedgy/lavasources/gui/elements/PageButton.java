package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public class PageButton extends GuiButton{

	public enum EnumPageButtonType{
		NEXT_PAGE(GuiLocation.GuiLocations.next_page),
		PREVIOUS_PAGE(GuiLocation.GuiLocations.previous_page)
		;

		public final GuiLocation GENERAL, HOVER, DISABLED;

		private EnumPageButtonType(GuiLocation general){ this(general, general, general); }

		private EnumPageButtonType(GuiLocation general, GuiLocation hover, GuiLocation disabled){
			this.DISABLED = disabled;
			this.GENERAL = general;
			this.HOVER = hover;
		}
	}

	private final EnumPageButtonType type;
	private ModGui drawer;

	public PageButton(int buttonId, int x, int y, String buttonText, EnumPageButtonType type){
		super(buttonId, x, y, buttonText, type.GENERAL, type.HOVER, type.DISABLED, null, null);
		this.type = type;
	}

	public void setDrawer(ModGui drawer){
		this.drawer = drawer;
	}

	@Override
	public void displayToolTip(Minecraft mc, int mouseX, int mouseY) { }

	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
		if(hovered) displayToolTip(null, mouseX, mouseY);
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		drawButton(null, mouseX, mouseY, partialTicks);
	}
}
