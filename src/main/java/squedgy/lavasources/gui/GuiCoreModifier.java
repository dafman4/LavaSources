package squedgy.lavasources.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squedgy.lavasources.gui.elements.ElementFillable;
import squedgy.lavasources.inventory.ContainerCoreModifier;

import static squedgy.lavasources.gui.GuiCoreModifier.EnumFields.*;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.HORIZONTAL_FILL;
import static squedgy.lavasources.gui.elements.ElementFillable.EnumFillableType.VERTICAL_FILL;
import static squedgy.lavasources.helper.GuiLocation.GuiLocations.energy_fill;
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
	}

	@Override
	protected void setElements() {
		addElement(new ElementFillable(this, 7, 4, INVENTORY, FLUIDS_AMOUNT.ordinal(), FLUID_CAPACITY.ordinal(), VERTICAL_FILL,
						() -> {
							FluidStack ret;
							ret = POSSIBLE_FLUIDS.get(this.INVENTORY.getField(FLUID_INDEX.ordinal()));
							return ret;
						}
				)
		);
		addElement(new ElementFillable(this, 75, 21, INVENTORY, TICKS_FILLING.ordinal(), FILL_TIME.ordinal(), HORIZONTAL_FILL));
		addElement(new ElementFillable(this, 161, 4, INVENTORY, ENERGY_AMOUNT.ordinal(), MAX_ENERGY_STORED.ordinal(), VERTICAL_FILL, energy_fill));
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		String s = INVENTORY.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s,xSize/2-this.fontRenderer.getStringWidth(s)/2, 6, 0x404040);
		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8 , 40, 0x404040);
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {}
	
	private int getLevel(int pixelHeight, int amount ,int max){ return amount * pixelHeight / max; }


}
