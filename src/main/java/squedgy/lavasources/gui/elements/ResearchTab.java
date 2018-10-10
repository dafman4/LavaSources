package squedgy.lavasources.gui.elements;

import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResearchTab {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	private static final List<ResearchTab> TABS = new ArrayList();
	private final String TAB_NAME;
	private final List<Research> RELATED_RESEARCH = new ArrayList();
	private final GuiLocation TAB_BACKGROUND;

	public ResearchTab(String tabName,GuiLocation background, Research... relatedResearch){
		TAB_NAME = tabName;
		RELATED_RESEARCH.addAll(Arrays.asList(relatedResearch));
		TAB_BACKGROUND = background;
	}
	public  ResearchTab(String tabName, Research... relatedResearch){
		this(tabName, GuiLocation.DEFAULT_SCROLLABLE_BACKGROUND, relatedResearch);
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

	public List<Research> getRelatedResearch(){ return new ArrayList(RELATED_RESEARCH); }

	public void addRelatedResearch(Research r){ if(RELATED_RESEARCH.indexOf(r) < 0) RELATED_RESEARCH.add(r); }

	public String getTabName(){ return TAB_NAME; }

	public GuiLocation getTabBackground(){ return TAB_BACKGROUND; }

//</editor-fold>

}
