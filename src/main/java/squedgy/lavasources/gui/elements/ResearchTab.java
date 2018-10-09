package squedgy.lavasources.gui.elements;

import com.google.common.collect.Lists;
import squedgy.lavasources.research.Research;

import java.util.Arrays;
import java.util.List;

public class ResearchTab {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static final List<ResearchTab> TABS = Lists.newArrayList();
	private final String TAB_NAME;
	private final List<Research> RELATED_RESEARCH = Lists.newArrayList();

	public ResearchTab(String tabName, Research... relatedResearch){
		TAB_NAME = tabName;
		RELATED_RESEARCH.addAll(Arrays.asList(relatedResearch));
	}


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static List<ResearchTab> getAllTabs(){ return TABS; }

	public static void addNewTab(ResearchTab tab){
		if(TABS.indexOf(tab) < 0){
			TABS.add(tab);
			ResearchTab.addNewTab(tab);
		}
	}

	public List<Research> getRelatedResearch(){ return Lists.newArrayList(RELATED_RESEARCH); }

	public void addRelatedResearch(Research r){ if(RELATED_RESEARCH.indexOf(r) < 0) RELATED_RESEARCH.add(r); }

	public String getTabName(){ return TAB_NAME; }

//</editor-fold>

}
