package squedgy.lavasources.gui.elements;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.helper.GuiLocation;

import static squedgy.lavasources.helper.GuiLocation.GuiLocations.*;

public class ElementFillable extends GuiElement{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private final int AMOUNT, MAX;
	private IFluidReturner fluidReturner = null;
	private GuiLocation fill, border;
	private EnumFillableType type;

	private ElementFillable(ModGui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, GuiLocation fill, IFluidReturner returner, GuiLocation border){
		super(parent, x, y, type.WIDTH - 2, type.HEIGHT - 2, container);
		AMOUNT = fieldAmount;
		MAX = fieldMax;
		fluidReturner = returner;
		this.fill = fill;
		this.border = border;
		this.type = type;
	}

	public ElementFillable(ModGui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, IFluidReturner returner){
		this(parent, x, y, container,fieldAmount, fieldMax, type, default_fill, returner, type.LOCATION);
	}
	public ElementFillable(ModGui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, IFluidReturner returner, GuiLocation border){
		this(parent, x, y, container,fieldAmount, fieldMax, type, default_fill, returner, border);
	}

	public ElementFillable(ModGui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type, GuiLocation fill){
		this(parent, x, y, container, fieldAmount, fieldMax, type, fill, null, type.LOCATION);
	}

	public ElementFillable(ModGui parent, int x, int y, IInventory container, int fieldAmount, int fieldMax, EnumFillableType type){
		this(parent, x, y, container, fieldAmount, fieldMax, type, default_fill);
	}

//</editor-fold>


	@Override
	public void drawGuiElementForeground(int mouseX, int mouseY) {
	}

	@Override
	public void drawGuiElementBackground(int mouseX, int mouseY, float partialTicks) {
		bindTexture(border);
		drawTexturedModal(0, 0, border.textureX, border.textureY, border.width, border.height);
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
				bindTexture(fill);
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
					if(sprite != null) drawTexturedModal(widthAddition, heightAddition, sprite, drawWidth, drawHeight);
					else drawTexturedModal(widthAddition, heightAddition, fill.textureX, fill.textureY, drawWidth, drawHeight);
				}
			}
		}

		if(this.type == EnumFillableType.WIDE_FILL){
			bindTexture(type.OVERLAY);
			drawTexturedModal(1 , 1, type.OVERLAY);
		}
	}

	public enum EnumFillableType{
		HORIZONTAL_FILL(fillable_horizontal),
		VERTICAL_FILL(fillable_vertical),
		WIDE_FILL(fillable_wide)
		;
		public final int WIDTH, HEIGHT;
		public final GuiLocation LOCATION, OVERLAY;
		EnumFillableType(GuiLocation location){
			this.LOCATION = location;
			this.WIDTH = location.width;
			this.HEIGHT = location.height;
			OVERLAY = ordinal() == 2 ?  fillable_wide_overlay : null;
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
