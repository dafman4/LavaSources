package squedgy.lavasources.research;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import squedgy.lavasources.gui.elements.ResearchTab;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResearchUtil {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final Map<ResearchTab, List<Research>> TAB_RESEARCH = new HashMap<>();

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static void addResearchToTab(ResearchTab tab, Research research){
		List<Research> list = TAB_RESEARCH.get(tab);
		if(list.indexOf(research) < 0) list.add(research);
	}

	public static void addAllResearchToTab(@Nonnull ResearchTab tab,@Nonnull Research[] research){
		for(Research r : research) addResearchToTab(tab, r);
	}

	public static List<Research> getTabsResearch(ResearchTab tab){ return TAB_RESEARCH.get(tab); }

	public static void addResearchTab(ResearchTab tab){ TAB_RESEARCH.putIfAbsent(tab, new ArrayList<>()); }

//</editor-fold>
}
