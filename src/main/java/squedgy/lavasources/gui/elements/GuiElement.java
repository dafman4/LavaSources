package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.generic.gui.IGuiElement;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public abstract class GuiElement implements IGuiElement {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected int locationX, locationY, height, width;
	protected IInventory container;
	protected ModGui drawer;

	public GuiElement(ModGui drawer, int locationX, int locationY, int width, int height, IInventory container){
		this.locationX = locationX;
		this.locationY = locationY;
		this.height = height;
		this.width = width;
		this.container = container;
		setDrawer(drawer);
	}

	public void drawGuiElementForeground(int mouseX, int mouseY) { }

	protected final TextureAtlasSprite getFluidSprite(FluidStack fluid){return mc.getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString()); }
	protected final void bindTextureAtlasTextures(){ bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE); }
	public static int getProgressOrFillLevel(int pixels, int amount, int max){ return amount * pixels / max;}
	protected final int getField(int fieldId){ return container != null ? container.getField(fieldId) : -1; }


	protected final void drawTexturedModal(int xAddition, int yAddition, TextureAtlasSprite sprite, int width, int height){
		drawer.drawTexturedModalRect(
				drawer.getHorizontalMargin() + locationX + xAddition,
				drawer.getVerticalMargin() + locationY + yAddition,
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
				drawer.getHorizontalMargin() + locationX + xAddition,
				drawer.getVerticalMargin() + locationY + yAddition,
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


	public final ModGui getDrawer(){ return drawer; }

	public final void setDrawer(ModGui drawer){
		this.drawer = drawer;
		if(mc != drawer.mc && drawer.mc != null) mc = drawer.mc;
	}
}
