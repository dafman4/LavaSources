package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.gui.IGuiElement;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.helper.GuiLocation.GuiLocations;

public abstract class GuiButton extends net.minecraft.client.gui.GuiButton implements IGuiElement {

	private GuiLocation normalImage, hoverImage, disabledImage, borderVertical, borderHorizontal;

	public GuiButton(int buttonId, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled, GuiLocation borderHorizontal, GuiLocation borderVertical){
		super(buttonId, x, y, normal.width, normal.height, buttonText);
		normalImage = normal;
		hoverImage = hover;
		disabledImage = disabled;
		this.borderHorizontal = borderHorizontal;
		this.borderVertical = borderVertical;
		LavaSources.writeMessage(getClass(), "x = " + this.x + "\ny = " + this.y);
	}

	public GuiButton(int buttonId, int x, int y, String buttonText){
		this(buttonId, x, y, buttonText, GuiLocations.default_button, GuiLocations.default_button_hover, GuiLocations.default_button_disabled, GuiLocations.book_border_horizontal, GuiLocations.book_border_vertical);
	}

	protected GuiLocation getDrawLocation(){
		if(!this.enabled) return  (disabledImage);
		else if(this.hovered) return (hoverImage);
		else return (normalImage);
	}

	protected final void doDefaultDraw(int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		int i = this.getHoverState(this.hovered);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	protected void drawButtonString(FontRenderer renderer){
		int j = 0xE0E0E0;

		if (!this.enabled)
		{
			j = 0xA0A0A0;
		}
		else if (this.hovered)
		{
			j = 0xFFFFA0;
		}

		this.drawCenteredString(renderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
	}

	protected void drawButtonBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks){
		hovered = (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y+height);
		GuiLocation draw = getDrawLocation();
		//draw border
		int yDraws = 0, xDraws = 0;
		if(borderVertical != null) {
			bindTexture(borderVertical.texture.location, mc);
			for (; yDraws < height / borderVertical.height; yDraws++) {
				drawTexturedModalRect(x, y + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, borderVertical.height);
				drawTexturedModalRect(x + width - borderVertical.width, y + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, borderVertical.height);
			}
			int drawHeight = height - borderVertical.height * yDraws;
			drawTexturedModalRect(x, y + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, drawHeight);
			drawTexturedModalRect(x + width - borderVertical.width, y + borderVertical.height * yDraws, borderVertical.textureX, borderVertical.textureY, borderVertical.width, drawHeight);
		}
		if(borderHorizontal != null) {
			bindTexture(borderHorizontal.texture.location, mc);
			for (; xDraws < height / borderHorizontal.height; xDraws++) {
				drawTexturedModalRect(x + borderHorizontal.width * xDraws, y, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
				drawTexturedModalRect(x + borderHorizontal.width * xDraws, y + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
			}
			int drawWidth = width - borderHorizontal.width * xDraws;
			drawTexturedModalRect(x + borderHorizontal.width * xDraws, y, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);
			drawTexturedModalRect(x + borderHorizontal.width * xDraws, y + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);
		}
		//draw background
		bindTexture(draw.texture.location, mc);
		for(xDraws = 0; xDraws < (width-2)/draw.width; xDraws++){
			for(yDraws = 0; yDraws < (height-2)/draw.height; yDraws++){
				drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, draw.height);
			}
			drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, height - draw.height*yDraws);
		}
		drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY,width - draw.width*xDraws, height - draw.height*yDraws);

	}

	public abstract void displayToolTip(Minecraft mc, int mouseX, int mouseY);

	@Override
	public final void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (this.visible)
		{
			this.doDefaultDraw(mouseX, mouseY);
			this.drawButtonBackground(mc, mouseX, mouseY, partialTicks);
			this.mouseDragged(mc, mouseX, mouseY);
			this.drawButtonString(mc.fontRenderer);
		}
	}

	public static void bindTexture(ResourceLocation r, Minecraft mc){
		mc.renderEngine.bindTexture(r);
	}

}
