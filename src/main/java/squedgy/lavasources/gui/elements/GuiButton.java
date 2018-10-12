package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import squedgy.lavasources.helper.GuiLocation;

import java.util.List;

public abstract class GuiButton extends net.minecraft.client.gui.GuiButton {

	private GuiLocation normalImage, hoverImage, disabledImage, borerVertical, borderHorizontal;

	public GuiButton(int buttonId, int x, int y, String buttonText, GuiLocation normal, GuiLocation hover, GuiLocation disabled, GuiLocation borderHorizontal, GuiLocation borderVertical){
		super(buttonId, x, y, normal.width, normal.height, buttonText);
		normalImage = normal;
		hoverImage = hover;
		disabledImage = disabled;
		this.borderHorizontal = borderHorizontal;
		this.borerVertical = borderVertical;
	}

	public GuiButton(int buttonId, int x, int y, String buttonText){
		this(buttonId, x, y, buttonText, GuiLocation.default_button, GuiLocation.default_button_hover, GuiLocation.default_button_disabled, GuiLocation.book_border_horizontal, GuiLocation.book_border_vertical);
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
		GuiLocation draw = getDrawLocation();
		//draw border
		mc.renderEngine.bindTexture(borerVertical.location);
		int yDraws = 0;
		for(; yDraws < height/borerVertical.height; yDraws++){
			drawTexturedModalRect(x, y + borerVertical.height*yDraws, borerVertical.textureX, borerVertical.textureY, borerVertical.width, borerVertical.height);
			drawTexturedModalRect(x + width - borerVertical.width, y + borerVertical.height*yDraws, borerVertical.textureX, borerVertical.textureY, borerVertical.width, borerVertical.height);
		}
		int drawHeight = height - borerVertical.height*yDraws;
		drawTexturedModalRect(x, y + borerVertical.height*yDraws, borerVertical.textureX, borerVertical.textureY, borerVertical.width, drawHeight);
		drawTexturedModalRect(x + width - borerVertical.width, y + borerVertical.height*yDraws, borerVertical.textureX, borerVertical.textureY, borerVertical.width, drawHeight);

		mc.renderEngine.bindTexture(borderHorizontal.location);
		int xDraws = 0;
		for(; xDraws < height/borderHorizontal.height; xDraws++){
			drawTexturedModalRect(x + borderHorizontal.width*xDraws, y, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
			drawTexturedModalRect(x + borderHorizontal.width*xDraws, y + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, borderHorizontal.width, borderHorizontal.height);
		}
		int drawWidth = width - borderHorizontal.width*xDraws;
		drawTexturedModalRect(x + borderHorizontal.width*xDraws, y, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);
		drawTexturedModalRect(x + borderHorizontal.width*xDraws, y + height - borderHorizontal.height, borderHorizontal.textureX, borderHorizontal.textureY, drawWidth, borderHorizontal.height);

		//draw background
		mc.renderEngine.bindTexture(draw.location);
		for(xDraws = 0; xDraws < (width-2)/draw.width; xDraws++){
			for(yDraws = 0; yDraws < (height-2)/draw.height; yDraws++){
				drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, draw.height);
			}
			drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY, draw.width, height - draw.height*yDraws);
		}
		drawTexturedModalRect(x + draw.width*xDraws, y + draw.height * draw.height, draw.textureX, draw.textureY,width - draw.width*xDraws, height - draw.height*yDraws);

	}

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

}
