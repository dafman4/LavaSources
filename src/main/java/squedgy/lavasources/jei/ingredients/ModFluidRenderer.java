package squedgy.lavasources.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.enums.EnumFluidTier;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.EnumConversions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class ModFluidRenderer implements IIngredientRenderer<FluidStack> {
	protected int ticks = 0;
	protected static final int divisor = EnumConversions.SECONDS_TO_TICKS.convertToInt(2),  ticksToChange =  EnumFluidTier.values().length* divisor;
	protected EnumFluidTier tierAt = EnumFluidTier.BASIC;
	protected final int MAX_PROGRESS_PIXELS;
	protected int pixelProgress;

	public ModFluidRenderer( int maxProgressPixels){
		this.MAX_PROGRESS_PIXELS = maxProgressPixels;
	}

	@Override
	public final void render(Minecraft mc, int i, int i1, @Nullable FluidStack fluidStack) {
		if(fluidStack != null) {
			tierAt = EnumFluidTier.values()[this.ticks / divisor];
			mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			TextureAtlasSprite fluid = mc.getTextureMapBlocks().getAtlasSprite(Objects.requireNonNull(fluidStack).getFluid().getStill().toString());
			pixelProgress = GuiElement.getProgressOrFillLevel(MAX_PROGRESS_PIXELS, fluidStack.amount, tierAt.CAPACITY);
			if(pixelProgress > MAX_PROGRESS_PIXELS) pixelProgress = MAX_PROGRESS_PIXELS;
			if(pixelProgress < 0 ) pixelProgress = 1;
			renderActual(mc, i, i1, fluidStack, fluid);
			this.ticks++;
			if (this.ticks >= ticksToChange) this.ticks = 0;
		}
	}

	protected abstract void renderActual(Minecraft mc, int i , int i1, @Nonnull FluidStack fluidStack, TextureAtlasSprite fluid);

	public abstract List<String> getTooltip(Minecraft minecraft, FluidStack ingredient, ITooltipFlag tooltipFlag);


}
