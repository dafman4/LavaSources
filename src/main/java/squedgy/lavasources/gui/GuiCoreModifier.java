package squedgy.lavasources.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.ModGui;
import squedgy.lavasources.inventory.ContainerCoreModifier;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.block.BlockFurnace;

/**
 *
 * @author David
 */
@SideOnly(Side.CLIENT)
public class GuiCoreModifier extends ModGui {
	
	private static final ResourceLocation GUI_LOCATION = new ResourceLocation(LavaSources.MOD_ID, "textures/gui/container/core_modifier.png");
    private final InventoryPlayer PLAYER_INVENTORY;
	private final IInventory INVENTORY;
	private boolean printed;
	public enum EnumFields{
		TICKS_FILLING,
		FLUIDS_AMOUNT,
		ENERGY_AMOUNT,
		FLUID_INDEX,
		FLUID_CAPACITY,
		MAX_ENERGY_STORED,
		MAKING
	}

	public GuiCoreModifier(InventoryPlayer player, IInventory inventory){
		super(new ContainerCoreModifier(player, inventory));
		INVENTORY = inventory;
		PLAYER_INVENTORY = player;
		LavaSources.writeMessage(getClass(), "ySize = " + ySize + ", xSize = " + xSize);
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
		String s = INVENTORY.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s,xSize/2-this.fontRenderer.getStringWidth(s)/2, 6, 4210752);
		this.fontRenderer.drawString(PLAYER_INVENTORY.getDisplayName().getUnformattedText(), 8 , 41, 4210752);
	}

	@Override
	protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(GUI_LOCATION);
		int marginHorizontal = (width - xSize) / 2, marginVertical = (height - ySize) / 2;
		this.drawTexturedModalRect(marginHorizontal, marginVertical, 0, 0, xSize, ySize);
		if(INVENTORY.getField(EnumFields.MAKING.ordinal()) >= 0){
			int progressLevel = getProgressLevel(24);
			this.drawTexturedModalRect(marginHorizontal + 76, marginVertical + 22, 176, 0, progressLevel, 6);
		}
		int energyLevel = getEnergyLevel(32);
		this.drawTexturedModalRect(marginHorizontal + 162, marginVertical + 39 - energyLevel, 176, 70-energyLevel, 6, energyLevel);

        int fluidIndex = INVENTORY.getField(EnumFields.FLUID_INDEX.ordinal()), fluidTextureX = 200, fluidTextureY = 38;
        if(fluidIndex > -1){
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(TileEntityCoreModifier.POSSIBLE_FLUIDS.get(fluidIndex).getFluid().getStill().toString());
            if(!printed) {
                LavaSources.writeMessage(getClass(), TileEntityCoreModifier.POSSIBLE_FLUIDS.get(fluidIndex).getFluid().getStill().toString());
                LavaSources.writeMessage(getClass(), sprite.toString());
                printed = true;
            }

            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int fluidLevel = getFluidLevel(32);
            while(fluidLevel > sprite.getIconHeight()){
                this.drawTexturedModalRect(marginHorizontal + 8, marginVertical + 39 - fluidLevel, sprite, 6, sprite.getIconHeight());
                fluidLevel -= sprite.getIconHeight();
            }
            this.drawTexturedModalRect(marginHorizontal + 8, marginVertical + 39 - fluidLevel, sprite, 6, fluidLevel);
        }
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
