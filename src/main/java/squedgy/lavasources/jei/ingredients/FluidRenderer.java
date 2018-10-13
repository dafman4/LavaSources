package squedgy.lavasources.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.enums.EnumEnergyTier;
import squedgy.lavasources.enums.EnumFluidTier;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.EnumConversions;
import squedgy.lavasources.jei.Plugin;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FluidRenderer implements IIngredientRenderer<FluidStack> {

	private int i = 0;
	private static final int divisor = EnumConversions.SECONDS_TO_TICKS.convertToInt(2),  ticksToChange =  EnumFluidTier.values().length* divisor;

	@Override
	public void render(Minecraft mc, int i, int i1, @Nullable FluidStack fluidStack) {
		if(fluidStack!= null) {
			EnumFluidTier fluidTier = EnumFluidTier.values()[this.i/divisor];
			mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			TextureAtlasSprite fluid = mc.getTextureMapBlocks().getAtlasSprite(Objects.requireNonNull(fluidStack).getFluid().getStill().toString());
			int progress = GuiElement.getProgressOrFillLevel(32, fluidStack.amount, fluidTier.CAPACITY);
			if(progress > 32) progress = 32;
			Objects.requireNonNull(mc.currentScreen).drawTexturedModalRect(i, i1 + 32 - progress, fluid, 6, progress);
		}
		this.i++;
		if(this.i >= ticksToChange ) this.i = 0;
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, FluidStack ingredient, ITooltipFlag tooltipFlag) {
		return Arrays.asList(ingredient.getLocalizedName() + " (" + ingredient.amount + " / " +EnumFluidTier.values()[i/divisor].CAPACITY + ")",
				ingredient.amount/ TileEntityCoreModifier.FILL_TIME+" mb/t"
		);
	}
}
