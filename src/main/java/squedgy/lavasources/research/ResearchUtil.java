package squedgy.lavasources.research;

import com.google.common.collect.Maps;
import squedgy.lavasources.gui.elements.ResearchTab;

import java.util.List;
import java.util.Map;

public class ResearchUtil {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final Map<ResearchTab, List<Research>> TAB_RESEARCH = Maps.newHashMap();

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static void addResearchToTab(ResearchTab tab, Research research){
		List<Research> list = TAB_RESEARCH.get(tab);
		if(list.indexOf(research) < 0) list.add(research);
	}

	public static List<Research> getTabsResearch(ResearchTab tab){
		return TAB_RESEARCH.get(tab);
	}

//</editor-fold>
}
