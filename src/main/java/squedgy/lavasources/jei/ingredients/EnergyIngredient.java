package squedgy.lavasources.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextComponentTranslation;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.enums.EnumEnergyTier;
import squedgy.lavasources.enums.EnumFluidTier;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.helper.EnumConversions;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnergyIngredient  {

	public static final IIngredientType<EnergyIngredient> TYPE = new IngredientType();

	public final int max;
	private int amount;

	public EnergyIngredient(int max, int amount){
		this.max = max;
		this.amount = amount;
	}

	public EnergyIngredient(EnergyIngredient e){ this(e.max, e.amount); }

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public EnergyIngredient copy(){
		return new EnergyIngredient(this);
	}

	public static class IngredientType implements IIngredientType<EnergyIngredient>{

		@Override
		public Class<? extends EnergyIngredient> getIngredientClass() {
			return EnergyIngredient.class;
		}

	}

	public static class IngredientHelper implements IIngredientHelper<EnergyIngredient>{

		@Override
		public List<EnergyIngredient> expandSubtypes(List<EnergyIngredient> list) {
			return list;
		}

		@Nullable
		@Override
		public EnergyIngredient getMatch(Iterable<EnergyIngredient> iterable, EnergyIngredient energyIngredient) {
			for (EnergyIngredient i : iterable) {
				if (i != null && i.equals(energyIngredient)) return i;
			}
			return null;
		}

		@Override
		public String getDisplayName(EnergyIngredient energyIngredient) {
			TextComponentTranslation t = new TextComponentTranslation("jei.ingredient.energy");
			if(t.getUnformattedComponentText().isEmpty())return "Energy";
			return t.getUnformattedComponentText();
		}

		@Override
		public String getUniqueId(EnergyIngredient energyIngredient) {
			return "lavasources.energy";
		}

		@Override
		public String getWildcardId(EnergyIngredient energyIngredient) {
			return null;
		}

		@Override
		public String getModId(EnergyIngredient energyIngredient) {
			return LavaSources.MOD_ID;
		}

		@Override
		public Iterable<Color> getColors(EnergyIngredient energyIngredient) {
			return new ArrayList<>();
		}

		@Override
		public String getResourceId(EnergyIngredient energyIngredient) {
			return "energy";
		}

		@Override
		public EnergyIngredient copyIngredient(EnergyIngredient energyIngredient) {
			return energyIngredient.copy();
		}

		@Override
		public String getErrorInfo(@Nullable EnergyIngredient energyIngredient) {
			if(energyIngredient == null) return "Ingredient was null";
			return "Unknown issue!";
		}
	}

	public static class Renderer implements IIngredientRenderer<EnergyIngredient>{

		private int i = 0;
		private static final int divisor = EnumConversions.SECONDS_TO_TICKS.convertToInt(2), ticksToChange =  EnumFluidTier.values().length * divisor;

		public final int WIDTH = 6;

		@Override
		public List<String> getTooltip(Minecraft minecraft, EnergyIngredient ingredient, ITooltipFlag tooltipFlag) {
			return Arrays.asList(
					"RF (" + ingredient.amount + "/" + EnumEnergyTier.values()[i/divisor].CAPACITY + ")",
					ingredient.amount/ TileEntityCoreModifier.FILL_TIME + " RF/t"
			);
		}

		@Override
		public void render(Minecraft mc, int i, int i1,  @Nullable EnergyIngredient e) {
			if(e != null) {
				GuiLocation g = GuiLocation.energy_fill;
				if(g != null) {
					EnumEnergyTier energy = EnumEnergyTier.values()[this.i / divisor];
					mc.renderEngine.bindTexture(g.location);
					int progress = GuiElement.getProgressOrFillLevel(32, e.amount, energy.CAPACITY);
					if(progress > 32) progress = 32;
					int draws = 0;
					for(;draws < progress/g.height; draws++){
						mc.currentScreen.drawTexturedModalRect(i, i1 + 32 - g.height * (draws + 1), g.textureX, g.textureY, WIDTH, g.height);
					}
					int height = (progress - g.height*draws);
					mc.currentScreen.drawTexturedModalRect(i , i1 + 32 - g.height * draws - height, g.textureX, g.textureY, WIDTH, height);
				}
				this.i++;
				if(this.i >= ticksToChange ) this.i = 0;
			}
		}
	}

}
