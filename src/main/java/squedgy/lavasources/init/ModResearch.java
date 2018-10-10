package squedgy.lavasources.init;

import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.item.ModItem;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchUtil;

public class ModResearch {

	public static ResearchTab MAIN = new ResearchTab("main_tab");

	public static Research TEST = new Research("test");

	public static class RegistryHandler{

		public static void register(){
			Research.addNewResearch(TEST);
			ResearchUtil.addResearchTab(MAIN);
			ResearchUtil.addAllResearchToTab(MAIN , new Research[] {TEST});
		}

	}
}
