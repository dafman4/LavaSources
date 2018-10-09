package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.helper.GuiLocation;

public abstract class GuiElement {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	protected int locationX, locationY, height, width;
	protected IInventory container;
	protected final Gui DRAWER;

	public GuiElement(Gui drawer, int locationX, int locationY, int width, int height, IInventory container){
		this.locationX = locationX;
		this.locationY = locationY;
		this.height = height;
		this.width = width;
		this.container = container;
		this.DRAWER = drawer;
	}

	public abstract void drawGuiElement(int horizontalMargin, int verticalMargin);

	protected TextureAtlasSprite getFluidSprite(FluidStack fluid){return mc.getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString()); }
	protected void bindTextureAtlasTextures(){ mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE); }
	protected static int getProgressOrFillLevel(int pixels, int amount, int max){ return amount * pixels / max;}
	protected int getField(int fieldId){ return container != null ? container.getField(fieldId) : -1; }


	protected void drawTexturedModal(int horizontalMargin, int verticalMargin, int xAddition, int yAddition, TextureAtlasSprite sprite, int width, int height){
		DRAWER.drawTexturedModalRect(
				horizontalMargin + locationX + xAddition,
				verticalMargin + locationY + yAddition,
				sprite,
				width,
				height
		);
	}

	protected void drawTexturedModal(int horizontalMargin, int verticalMargin, int xAddition, int yAddition, int textX, int textY, int width, int height){
		DRAWER.drawTexturedModalRect(
				horizontalMargin + locationX + xAddition,
				verticalMargin + locationY + yAddition,
				textX,
				textY,
				width,
				height
		);
	}

	protected void bindTexture(GuiLocation texture){ bindTexture(texture.location); }
	protected void bindTexture(ResourceLocation texture){ mc.renderEngine.bindTexture(texture); }

}
