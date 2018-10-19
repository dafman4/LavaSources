package squedgy.lavasources.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
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
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

public abstract class GuiElement{
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

	public final void drawSecondGuiBackground(int mouseX, int mouseY, float partialTicks) { }
	public final void drawSecondGuiForeground(int mouseX, int mouseY) { }
	public ElementSubDisplay getSecondGui(){ return null;}

	public void init(){}
	public boolean close(){ return true;}
	public int getGuiLeft(){ return drawer.getGuiLeft(); }
	public int getGuiTop() { return  drawer.getGuiTop(); }
}
