package squedgy.lavasources.gui.elements;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

import java.util.ArrayList;
import java.util.List;

public class ResearchTab  extends IForgeRegistryEntry.Impl<ResearchTab>{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final List<ResearchTab> TABS = new ArrayList();
	private String TAB_NAME;
	private List<Research> RELATED_RESEARCH = new ArrayList();
	private String[] researchNames;
	private GuiLocation tabBackground;

	public ResearchTab(String tabName, String resourceLocation, GuiLocation background, String... relatedResearch){
		TAB_NAME = tabName;
		researchNames = relatedResearch;
		tabBackground = background;
		if(resourceLocation.indexOf(':') < 0) setRegistryName(LavaSources.MOD_ID, resourceLocation);
		else setRegistryName(resourceLocation);
		LavaSources.writeMessage(getClass(), this.toString());
	}
	public  ResearchTab(String tabName, String resourceLocation, String... relatedResearch){ this(tabName, resourceLocation, GuiLocation.getGuiLocation("default_scrollable_background"), relatedResearch); }

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

	public GuiLocation getTabBackground(){ return tabBackground == null ? GuiLocation.default_scrollable_background : tabBackground; }

	public void init(){
		for(String s : researchNames) RELATED_RESEARCH.add(GameRegistry.findRegistry(Research.class).getValue(new ResourceLocation(s)));
		researchNames = null;
	}

//</editor-fold>

	@Override
	public String toString(){
		return "ResearchTab{background = " + this.tabBackground +", displayName = " +this.TAB_NAME + "}";
	}

	public static void initAllTabs(){
		GameRegistry.findRegistry(ResearchTab.class).getValuesCollection().forEach(ResearchTab::init);
	}

	public static ResearchTab getResearchTab(ResourceLocation rl){ return CustomRegistryUtil.getRegistryEntry(ResearchTab.class, rl); }
	public static ResearchTab getResearchTab(String name){ return CustomRegistryUtil.getRegistryEntry(ResearchTab.class, name); }

}
