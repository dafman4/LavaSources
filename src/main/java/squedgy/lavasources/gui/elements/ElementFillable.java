package squedgy.lavasources.gui.elements;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.GuiLocation;

import javax.swing.*;

import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.*;
import static squedgy.lavasources.helper.GuiLocation.*;

public class ElementFillable extends GuiElement{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private final int AMOUNT, MAX;
	private IFluidReturner fluidReturner = null;
	private GuiLocation fill, border;
	private EnumFillableType type;

	private ElementFillable(Gui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, GuiLocation fill, IFluidReturner returner, GuiLocation border){
		super(parent, x, y, type.WIDTH - 2, type.HEIGHT - 2, container);
		AMOUNT = fieldAmount;
		MAX = fieldMax;
		fluidReturner = returner;
		this.fill = fill;
		this.border = border;
		this.type = type;
	}

	public ElementFillable(Gui parent,int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, IFluidReturner returner){
		this(parent, x, y, container,fieldAmount, fieldMax, type, default_fill, returner, type.LOCATION);
	}
	public ElementFillable(Gui parent,int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, IFluidReturner returner, GuiLocation border){
		this(parent, x, y, container,fieldAmount, fieldMax, type, default_fill, returner, border);
	}

	public ElementFillable(Gui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, GuiLocation fill){
		this(parent, x, y, container, fieldAmount, fieldMax, type, fill, null, type.LOCATION);
	}

	public ElementFillable(Gui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type){
		this(parent, x, y, container, fieldAmount, fieldMax, type, default_fill);
	}

//</editor-fold>

	@Override
	public void drawGuiElement(int horizontalMargin, int verticalMargin) {
		mc.renderEngine.bindTexture(border.location);
		drawTexturedModal(horizontalMargin, verticalMargin, 0, 0, border.textureX, border.textureY, border.width, border.height);
		if (container.getField(AMOUNT) > 0){
			int height= type.getProgressOrHeight(this.height, container.getField(AMOUNT), container.getField(MAX)),
				width = type.getProgressOrWidth(this.width, container.getField(AMOUNT), container.getField(MAX)),
				textureHeight, textureWidth;
			TextureAtlasSprite sprite = null;
			if(fluidReturner != null){
				bindTextureAtlasTextures();
				sprite = getFluidSprite(fluidReturner.getFluid());
				textureHeight = sprite.getIconHeight();
				textureWidth = sprite.getIconWidth();
			}else{
				mc.renderEngine.bindTexture(fill.location);
				textureHeight = fill.height;
				textureWidth = fill.width;
			}
			int heightDrawings = height/textureHeight,
			widthDrawings = width/textureWidth;
			for(int heightDrawingNumber = 0; heightDrawingNumber <= heightDrawings; heightDrawingNumber++){
				for(int widthDrawingNumber = 0; widthDrawingNumber <= widthDrawings; widthDrawingNumber++){
					int widthAddition = 1 + textureWidth * widthDrawingNumber,
						heightAddition = 1 + this.height - (textureHeight * heightDrawingNumber),
						drawHeight = textureHeight,
						drawWidth = textureWidth;
					if(widthDrawings == 0) drawWidth = width;
					else if(widthDrawingNumber == widthDrawings) drawWidth = width-(textureWidth * (widthDrawingNumber));
					if(heightDrawings == 0) drawHeight = height;
					else if(heightDrawingNumber == heightDrawings) drawHeight = height - (textureHeight * (heightDrawingNumber));

					heightAddition -= drawHeight;
					if(sprite != null) drawTexturedModal(horizontalMargin, verticalMargin, widthAddition, heightAddition, sprite, drawWidth, drawHeight);
					else drawTexturedModal(horizontalMargin, verticalMargin, widthAddition, heightAddition, fill.textureX, fill.textureY, drawWidth, drawHeight);
				}
			}
		}
	}

	public enum EnumFillableType{
		HORIZONTAL_FILL(GuiLocation.fillable_horizontal),
		VERTICAL_FILL(GuiLocation.fillable_vertical),
		WIDE_FILL(GuiLocation.fillable_wide)
		;
		public final int WIDTH, HEIGHT;
		public final GuiLocation LOCATION;
		EnumFillableType(GuiLocation location){
			this.LOCATION = location;
			this.WIDTH = location.width;
			this.HEIGHT = location.height;
		}

		public int getProgressOrHeight(int height, int amount, int max){
			if(VERTICAL_FILL == this || WIDE_FILL == this) return getProgressOrFillLevel(height, amount, max);
			return height;
		}

		public int getProgressOrWidth(int width, int amount, int max){
			if(HORIZONTAL_FILL == this) return getProgressOrFillLevel(width, amount, max);
			return width;
		}
	}

	public interface IFluidReturner{
		public abstract FluidStack getFluid();
	}
}
