package squedgy.lavasources.jei.ingredients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class LiquefierFluidRenderer extends ModFluidRenderer {

	public static final int WIDTH = 30;

	public LiquefierFluidRenderer(){ super(32);}

	@Override
	public void renderActual(Minecraft mc, int i, int i1, @Nonnull FluidStack fluidStack, TextureAtlasSprite fluid) {
		mc.currentScreen.drawTexturedModalRect(i ,i1 + 32 - pixelProgress, fluid, WIDTH, pixelProgress);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, FluidStack ingredient, ITooltipFlag tooltipFlag) {
		return Arrays.asList(ingredient.getLocalizedName() + " (" + ingredient.amount + "/" +  tierAt.CAPACITY + ")");
	}

}
