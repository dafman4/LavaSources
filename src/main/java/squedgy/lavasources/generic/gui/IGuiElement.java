package squedgy.lavasources.generic.gui;

public interface IGuiElement {
	public abstract void drawGuiElementForeground(int mouseX, int mouseY);
	public abstract void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks);
}
