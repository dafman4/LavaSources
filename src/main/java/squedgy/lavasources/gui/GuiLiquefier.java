package squedgy.lavasources.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.ModGui;
import squedgy.lavasources.inventory.ContainerLiquefier;

/**
 *
 * @author David
 */
public class GuiLiquefier extends ModGui {
	
	private static final ResourceLocation GUI_LOCATION = new ResourceLocation(LavaSources.MOD_ID, "textures/gui/container/liquefier.png");
	private final InventoryPlayer PLAYER_INVENTORY;
	private final IInventory INVENTORY;
	private final int FLUID_X = 176, FLUID_Y = 33,
		ENERGY_X = 176, ENERGY_Y = 65,
		ENERGY_WIDTH = 6, FLUID_WIDTH = 60;
	public enum EnumFields{
		FLUIDS_AMOUNT,
		ENERGY_AMOUNT,
		FLUID_CAPACITY,
		MAX_ENERGY_STORED,
		LIQUEFYING
	}
	
	public GuiLiquefier(InventoryPlayer player, IInventory inventory){
		super(new ContainerLiquefier(player, inventory));
		this.PLAYER_INVENTORY = player;
		this.INVENTORY = inventory;
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY){
		String s = INVENTORY.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s, xSize/4-this.fontRenderer.getStringWidth(s)/2, 6,0x404040);
		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8, 41, 0x404040);
	}
	
	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(GUI_LOCATION);
		int marginHorizontal = (width - xSize) / 2, marginVertical = (height-ySize) / 2;
		this.drawTexturedModalRect(marginHorizontal, marginVertical, 0,0, xSize, ySize);
		int energyLevel = getEnergyLevel(32);
		this.drawTexturedModalRect(marginHorizontal + 8, marginVertical + 39 - energyLevel, ENERGY_X, ENERGY_Y - energyLevel, ENERGY_WIDTH, energyLevel);
		int fluidLevel = getFluidLevel(32);
		this.drawTexturedModalRect(marginHorizontal + 108, marginVertical + 39 - fluidLevel, FLUID_X, FLUID_Y - fluidLevel, FLUID_WIDTH, fluidLevel);
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
