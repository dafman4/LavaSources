package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public abstract class GuiElement{
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected int locationX, locationY, height, width;
	protected IInventory container;
	private ModGui drawer;

	public GuiElement(ModGui drawer, int locationX, int locationY, int width, int height, IInventory container){
		this.locationX = locationX;
		this.locationY = locationY;
		this.height = height;
		this.width = width;
		this.container = container;
		this.drawer = drawer;
	}

	public void drawGuiElementForeground(int mouseX, int mouseY) { }
	public abstract void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks);

	protected final TextureAtlasSprite getFluidSprite(FluidStack fluid){return mc.getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString()); }
	protected final void bindTextureAtlasTextures(){ bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE); }
	public static int getProgressOrFillLevel(int pixels, int amount, int max){ return amount * pixels / max;}
	protected final int getField(int fieldId){ return container != null ? container.getField(fieldId) : -1; }


	protected final void drawTexturedModal(int xAddition, int yAddition, TextureAtlasSprite sprite, int width, int height){
		drawer.drawTexturedModalRect(
				getGuiLeft() + locationX + xAddition,
				getGuiTop() + locationY + yAddition,
				sprite,
				width,
				height
		);
	}

	protected final void drawString(int xAddition, int yAddition, String toDraw){
		drawString(xAddition, yAddition, toDraw, 0x404040);
	}

	protected final void drawString(int xAddition, int yAddition, String toDraw, int color){
		drawer.getFontRenderer().drawString(toDraw, locationX + xAddition, locationY + yAddition, color);
	}

	protected final void drawTexturedModal(int xAddition, int yAddition, GuiLocation toDraw){
		drawTexturedModal(xAddition, yAddition, toDraw.textureX, toDraw.textureY, toDraw.width, toDraw.height);
	}

	protected final void drawTexturedModal(int xAddition, int yAddition, int textX, int textY, int width, int height){
		drawer.drawTexturedModalRect(
				getGuiLeft() + locationX + xAddition,
				getGuiTop() + locationY + yAddition,
				textX,
				textY,
				width,
				height
		);
	}

	protected final void drawString(String toDraw, int xAddition, int yAddition){
		drawString(toDraw, xAddition, yAddition, 0xff040404);
	}

	protected final void drawString(String toDraw, int xAddition, int yAddition, int color){
		drawer.drawString(mc.fontRenderer, toDraw, locationX + xAddition, locationY + yAddition, color);
	}

	protected final void bindTexture(GuiLocation texture){ bindTexture(texture.texture.location); }
	protected final void bindTexture(ResourceLocation texture){ mc.renderEngine.bindTexture(texture); }
	protected final void drawRectUsingTessellator(GuiLocation image){
		drawRectUsingTessellator(0, 0, image);
	}

	protected final void drawRectUsingTessellator(int xAddition, int yAddition, GuiLocation image){
		drawRectUsingTessellator(
			locationX + xAddition + getGuiLeft() ,
			locationY + yAddition + getGuiTop(),
			image.width,
			image.height,
			image.minU,
			image.maxU,
			image.minV,
			image.maxV
		);
	}

	protected final void drawRectUsingTessellator(int locationX, int locationY, int width, int height, float minU, float maxU, float minV, float maxV){
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		b.pos(locationX , locationY + height, 0).tex(minU, maxV).endVertex();
		b.pos(locationX + width, locationY + height, 0).tex(maxU, maxV).endVertex();
		b.pos(locationX + width, locationY , 0).tex(maxU, minV).endVertex();
		b.pos(locationX, locationY, 0).tex(minU, minV).endVertex();
		t.draw();
	}


	public final ModGui getDrawer(){ return drawer; }

	public final void setDrawer(ModGui drawer){
		this.drawer = drawer;
		if(mc != drawer.mc && drawer.mc != null) mc = drawer.mc;
		extraSetDrawers(drawer);
	}

	public void extraSetDrawers(ModGui drawer){}

	public boolean drawsOnPhase(ModGui.EnumDrawPhase phase){
		return phase == ModGui.EnumDrawPhase.BACKGROUND || phase == ModGui.EnumDrawPhase.FOREGROUND;
	}

	public final void drawSecondGuiBackground(int mouseX, int mouseY, float partialTicks) {
		ElementSubDisplay display = getSecondGui();
		if(display != null) {
			if (display.drawsOnPhase(ModGui.EnumDrawPhase.BACKGROUND) || display.drawsOnPhase(ModGui.EnumDrawPhase.BUTTONS)) display.drawGuiElementBackground(mouseX, mouseY, partialTicks);
			if (display.drawsOnPhase(ModGui.EnumDrawPhase.SECONDARY_GUI_BACKGROUND)) display.drawSecondGuiBackground(mouseX, mouseY, partialTicks);
		}
	}
	public final void drawSecondGuiForeground(int mouseX, int mouseY) {
		ElementSubDisplay display = getSecondGui();
		if(display != null) {
			if (display.drawsOnPhase(ModGui.EnumDrawPhase.SECONDARY_GUI_BACKGROUND)) {
				if (display.drawsOnPhase(ModGui.EnumDrawPhase.SECONDARY_GUI_FOREGROUND)) display.drawSecondGuiForeground(mouseX, mouseY);
			} else if (display.drawsOnPhase(ModGui.EnumDrawPhase.FOREGROUND)) display.drawGuiElementForeground(mouseX, mouseY);
		}

	}
	public ElementSubDisplay getSecondGui(){ return null;}

	protected void drawCenteredString(String buttonText, int posX, int posY, int color){
		drawer.drawCenteredString(drawer.mc.fontRenderer, buttonText, posX, posY, color);
	}

	public void init(){}
	public boolean close(){ return true;}
	public int getGuiLeft(){ return drawer.getGuiLeft(); }
	public int getGuiTop() { return  drawer.getGuiTop(); }

	public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		float f = (float)(startColor >> 24 & 255) / 255.0F;
		float f1 = (float)(startColor >> 16 & 255) / 255.0F;
		float f2 = (float)(startColor >> 8 & 255) / 255.0F;
		float f3 = (float)(startColor & 255) / 255.0F;
		float f4 = (float)(endColor >> 24 & 255) / 255.0F;
		float f5 = (float)(endColor >> 16 & 255) / 255.0F;
		float f6 = (float)(endColor >> 8 & 255) / 255.0F;
		float f7 = (float)(endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double)right, (double)top, (double)0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double)left, (double)top, (double)0).color(f1, f2, f3, f).endVertex();
		bufferbuilder.pos((double)left, (double)bottom, (double)0).color(f5, f6, f7, f4).endVertex();
		bufferbuilder.pos((double)right, (double)bottom, (double)0).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
