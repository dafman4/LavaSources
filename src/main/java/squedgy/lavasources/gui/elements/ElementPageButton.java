package squedgy.lavasources.gui.elements;

import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public class ElementPageButton extends ElementButton {

	public enum EnumPageButtonType{
		NEXT_PAGE(GuiLocation.GuiLocations.next_page, GuiLocation.GuiLocations.next_page_hover),
		PREVIOUS_PAGE(GuiLocation.GuiLocations.previous_page, GuiLocation.GuiLocations.previous_page_hover)
		;

		public final GuiLocation GENERAL, HOVER, DISABLED;

		private EnumPageButtonType(GuiLocation general){ this(general, general); }

		private EnumPageButtonType(GuiLocation general, GuiLocation hover) { this(general, hover, general); }

		private EnumPageButtonType(GuiLocation general, GuiLocation hover, GuiLocation disabled){
			this.DISABLED = disabled;
			this.GENERAL = general;
			this.HOVER = hover;
		}
	}

	public ElementPageButton(ModGui drawer, int x, int y, String buttonText, EnumPageButtonType type){
		super(drawer, x, y, buttonText, type.GENERAL, type.HOVER, type.DISABLED);
	}

	@Override
	public void displayToolTip(int mouseX, int mouseY) {}

	@Override
	public String toString() {
		return "ElementPageButton{" +
				"locationX=" + locationX +
				", locationY=" + locationY +
				", height=" + height +
				", width=" + width +
				'}';
	}

	@Override
	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase) {
		return phase == ModGui.EnumDrawPhase.BUTTONS;
	}
}
