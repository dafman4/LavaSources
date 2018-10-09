package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.input.Keyboard;
import squedgy.lavasources.gui.elements.ElementFillable;
import squedgy.lavasources.gui.elements.ElementOverlay;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.inventory.ContainerLiquefier;

import static squedgy.lavasources.gui.GuiLiquefier.EnumFields.*;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.*;
import static squedgy.lavasources.helper.GuiLocation.*;

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
		addElement(new ElementFillable(this, 7, 4, inventory, ENERGY_AMOUNT.ordinal(), MAX_ENERGY_STORED.ordinal(), VERTICAL_FILL, ENERGY_FILL));
		addElement(new ElementFillable(this, 107, 5, inventory, FLUIDS_AMOUNT.ordinal(), FLUID_CAPACITY.ordinal(), WIDE_FILL, () -> FluidRegistry.getFluidStack(ModFluids.LIQUID_REDSTONE.getName(), 0), FILLABLE_WIDE));
		addElement(new ElementOverlay (this, 108, 6, inventory, FILLABLE_WIDE_OVERLAY));
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY){
		String s = INVENTORY.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s, xSize/4-this.fontRenderer.getStringWidth(s)/2, 6,0x404040);
		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8, 40, 0x404040);
	}
	
	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) { }

}
