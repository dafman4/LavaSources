package squedgy.lavasources.jei.ingredients;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.enums.EnumFluidTier;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CoreModifierFluidRenderer extends ModFluidRenderer {

	public CoreModifierFluidRenderer(){ super(32); }


	@Override
	public void renderActual(Minecraft mc, int i, int i1, @Nonnull FluidStack fluidStack, TextureAtlasSprite fluid) {
		Objects.requireNonNull(mc.currentScreen).drawTexturedModalRect(i, i1 + 32 - pixelProgress, fluid, 6, pixelProgress);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, FluidStack ingredient, ITooltipFlag tooltipFlag) {
		return Arrays.asList(ingredient.getLocalizedName() + " (" + ingredient.amount + " / " +tierAt.CAPACITY + ")",
				ingredient.amount/ TileEntityCoreModifier.FILL_TIME+" mb/t"
		);
	}
}
