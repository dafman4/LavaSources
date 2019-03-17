package squedgy.lavasources.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.text.TextComponentTranslation;
import squedgy.lavasources.LavaSources;

import javax.annotation.Nullable;

public abstract class ModCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

	protected IDrawable background, icon;
	protected TextComponentTranslation title;
	protected String uId;

	public ModCategory(IJeiHelpers helper, TextComponentTranslation title, String Uid){
		setBackground(helper);
		setIcon(helper);
		this.title = title;
		this.uId = Uid;
	}

	@Override
	public final String getUid() { return uId; }

	@Override
	public final String getTitle() { return title.getUnformattedText(); }

	@Override
	public final String getModName() { return LavaSources.NAME; }

	@Override
	public final IDrawable getBackground() { return background; }

	@Nullable
	@Override
	public final IDrawable getIcon() { return icon; }

	protected abstract void setBackground(IJeiHelpers helper);
	protected abstract void setIcon(IJeiHelpers helper);

}
