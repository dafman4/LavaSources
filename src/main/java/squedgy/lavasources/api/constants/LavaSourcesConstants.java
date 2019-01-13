package squedgy.lavasources.api.constants;

import squedgy.lavasources.helper.GuiLocation;

public abstract class LavaSourcesConstants {
	public static class Jei {
		public static final String CORE_MODIFIER_CATEGORY = "lavasources.core_modifier";
		public static final String LIQUEFIER_CATEGORY = "lavasources.liquefier";
	}

	public static class Gui{
		public static final int DEFAULT_TEXT_COLOR = 0xff000000;
		public static GuiLocation getDefaultGuiBackground(){ return GuiLocation.GuiLocations.default_gui; }
	}
}
