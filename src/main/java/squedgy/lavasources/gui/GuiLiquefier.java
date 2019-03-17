package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidRegistry;
import squedgy.lavasources.gui.elements.ElementFillable;
import squedgy.lavasources.gui.elements.ElementTextDisplay;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.inventory.ContainerLiquefier;

import java.util.Arrays;

import static squedgy.lavasources.gui.GuiLiquefier.EnumFields.*;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.*;
import static squedgy.lavasources.helper.GuiLocation.GuiLocations.*;

/**
 *
 * @author David
 */
public class GuiLiquefier extends ModGui {

	private final InventoryPlayer PLAYER_INVENTORY;
	private final IInventory INVENTORY;
	private final int FLUID_X = 176, FLUID_Y = 33,
		ENERGY_X = 176, ENERGY_Y = 65,
		ENERGY_WIDTH = 6, FLUID_WIDTH = 60;
	public enum EnumFields{
		FLUIDS_AMOUNT,
		ENERGY_AMOUNT,
		FLUID_CAPACITY,
		MAX_ENERGY_STORED
	}
	
	public GuiLiquefier(InventoryPlayer player, IInventory inventory){
		super(new ContainerLiquefier(player, inventory));
		this.PLAYER_INVENTORY = player;
		this.INVENTORY = inventory;
	}


	@Override
	protected void setElements(){
		addElement(new ElementFillable(this, 7, 4, INVENTORY, ENERGY_AMOUNT.ordinal(), MAX_ENERGY_STORED.ordinal(), VERTICAL_FILL, energy_fill));
		addElement(new ElementFillable(this, 107, 5, INVENTORY, FLUIDS_AMOUNT.ordinal(), FLUID_CAPACITY.ordinal(), WIDE_FILL, () -> FluidRegistry.getFluidStack(ModFluids.LIQUID_REDSTONE.getName(), 0), fillable_wide));
		String s = INVENTORY.getDisplayName().getUnformattedText();
		int sWidth = fontRenderer.getStringWidth(s);
		addElement(new ElementTextDisplay(this, 16, 6,sWidth, fontRenderer.FONT_HEIGHT, null, Arrays.asList(s)));
		addElement(new ElementTextDisplay(this, 8, 40, fontRenderer.getStringWidth(PLAYER_INVENTORY.getDisplayName().getUnformattedComponentText()), fontRenderer.FONT_HEIGHT, null, Arrays.asList(PLAYER_INVENTORY.getDisplayName().getUnformattedComponentText()) ));

	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY){
//		String s = INVENTORY.getDisplayName().getUnformattedText();
//		this.fontRenderer.drawString(s, xSize/4-this.fontRenderer.getStringWidth(s)/2, 6,0x404040);
//		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8, 40, 0x404040);
	}
	
	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

}
