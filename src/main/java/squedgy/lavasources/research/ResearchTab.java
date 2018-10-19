package squedgy.lavasources.research;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.GuiLocation;

import java.util.*;

public class ResearchTab  extends IForgeRegistryEntry.Impl<ResearchTab>{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private boolean buttonsUpdated = false;
	private String TAB_NAME;
	private List<ResearchButton> RELATED_RESEARCH = new ArrayList();
	private GuiLocation tabBackground;
	private final int height, width;

	public ResearchTab(String tabName, int width, int height,  String resourceLocation, GuiLocation background, ResearchButton... buttons){
		TAB_NAME = tabName;
		tabBackground = background;
		RELATED_RESEARCH.addAll(Arrays.asList(buttons));
		if(resourceLocation.indexOf(':') < 0) setRegistryName(LavaSources.MOD_ID, resourceLocation);
		else setRegistryName(resourceLocation);
		this.height = height;
		this.width = width;
	}
	public  ResearchTab(String tabName,int width, int height, String resourceLocation, ResearchButton... buttons){ this(tabName, width, height, resourceLocation, GuiLocation.getGuiLocation("default_scrollable_background"), buttons); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public List<ResearchButton> getRelatedResearch(){ return new ArrayList(RELATED_RESEARCH); }

	public void addRelatedResearch(ResearchButton r){ if(RELATED_RESEARCH.indexOf(r) < 0) RELATED_RESEARCH.add(r); }

	public String getTabName(){ return TAB_NAME; }

	public GuiLocation getTabBackground(){ return tabBackground == null ? GuiLocation.GuiLocations.default_scrollable_background : tabBackground; }

	public void updateButtons(){ buttonsUpdated = true; }

	public boolean isButtonsUpdated(){ return buttonsUpdated; }

	public void init(){ }

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
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
