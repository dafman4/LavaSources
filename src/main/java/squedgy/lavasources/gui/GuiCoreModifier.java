package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squedgy.lavasources.generic.ModGui;
import squedgy.lavasources.gui.elements.ElementFillable;
import squedgy.lavasources.inventory.ContainerCoreModifier;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

import static squedgy.lavasources.gui.GuiCoreModifier.EnumFields.*;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.HORIZONTAL_FILL;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.VERTICAL_FILL;
import static squedgy.lavasources.helper.GuiLocation.ENERGY_FILL;
import static squedgy.lavasources.tileentity.TileEntityCoreModifier.POSSIBLE_FLUIDS;

/**
 *
 * @author David
 */
@SideOnly(Side.CLIENT)
public class GuiCoreModifier extends ModGui {

    private final InventoryPlayer PLAYER_INVENTORY;
	private final IInventory INVENTORY;
	public enum EnumFields{
		TICKS_FILLING,
		FLUIDS_AMOUNT,
		ENERGY_AMOUNT,
		FLUID_INDEX,
		FLUID_CAPACITY,
		MAX_ENERGY_STORED,
		MAKING,
		FILL_TIME
	}

	public GuiCoreModifier(InventoryPlayer player, IInventory inventory){
		super(new ContainerCoreModifier(player, inventory));
		INVENTORY = inventory;
		PLAYER_INVENTORY = player;
		addElement(new ElementFillable(this, 7, 4,6,34, inventory, FLUIDS_AMOUNT.ordinal(), FLUID_CAPACITY.ordinal(), VERTICAL_FILL,
				() -> {
					FluidStack ret =null;
					ret = POSSIBLE_FLUIDS.get(this.INVENTORY.getField(FLUID_INDEX.ordinal()));
					return ret;
				}
			)
		);
		addElement(new ElementFillable(this, 75, 21, 24, 6, inventory, TICKS_FILLING.ordinal(), FILL_TIME.ordinal(), HORIZONTAL_FILL));
		addElement(new ElementFillable(this, 161, 4, 6, 34, inventory, ENERGY_AMOUNT.ordinal(), MAX_ENERGY_STORED.ordinal(), VERTICAL_FILL, ENERGY_FILL));
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		String s = INVENTORY.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s,xSize/2-this.fontRenderer.getStringWidth(s)/2, 6, 4210752);
		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8 , 41, 4210752);
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//		this.mc.getTextureManager().bindTexture(CORE_MODIFIER.location);
//		this.drawTexturedModalRect(getHorizontalMargin(), getVerticalMargin(), 0, 0, CORE_MODIFIER.width, CORE_MODIFIER.height);
//		int energyLevel = getEnergyLevel(32);
//		this.drawTexturedModalRect(getHorizontalMargin() + 162, getVerticalMargin() + 39 - energyLevel, 176, 70-energyLevel, 6, energyLevel);
	}

	@Override
	protected int getHorizontalMargin() {
		return (width - xSize) / 2;
	}

	@Override
	protected int getVerticalMargin() {
		return (height - ySize) / 2;
	}

	private int getProgressLevel(int pixelWidth){
		return getLevel(pixelWidth,
			INVENTORY.getField(EnumFields.TICKS_FILLING.ordinal()),
			TileEntityCoreModifier.FILL_TIME
		);
	}
	
	private int getLevel(int pixelHeight, int amount ,int max){
		return amount * pixelHeight / max;
	}
	
	private int getFluidLevel(int pixelHeight){
		return getLevel(pixelHeight,
			INVENTORY.getField(EnumFields.FLUIDS_AMOUNT.ordinal()),
			INVENTORY.getField(EnumFields.FLUID_CAPACITY.ordinal())
		);
	}
	
	private int getEnergyLevel(int pixelHeight){
		return getLevel(pixelHeight,
			INVENTORY.getField(EnumFields.ENERGY_AMOUNT.ordinal()),
			INVENTORY.getField(EnumFields.MAX_ENERGY_STORED.ordinal())
		);
	}

}
