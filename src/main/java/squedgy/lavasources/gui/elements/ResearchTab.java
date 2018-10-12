package squedgy.lavasources.gui.elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.helper.JsonWriting;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchButton;

import java.io.File;
import java.util.*;

public class ResearchTab  extends IForgeRegistryEntry.Impl<ResearchTab>{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private static final List<ResearchTab> TABS = new ArrayList();
	private boolean buttonsUpdated = false;
	private String TAB_NAME;
	private List<ResearchButton> RELATED_RESEARCH = new ArrayList();
	private GuiLocation tabBackground;

	public ResearchTab(String tabName, String resourceLocation, GuiLocation background, ResearchButton... buttons){
		TAB_NAME = tabName;
		tabBackground = background;
		RELATED_RESEARCH.addAll(Arrays.asList(buttons));
		if(resourceLocation.indexOf(':') < 0) setRegistryName(LavaSources.MOD_ID, resourceLocation);
		else setRegistryName(resourceLocation);
		if(!JsonWriting.writing)
		LavaSources.writeMessage(getClass(), this.toString());
	}
	public  ResearchTab(String tabName, String resourceLocation, ResearchButton... buttons){ this(tabName, resourceLocation, GuiLocation.getGuiLocation("default_scrollable_background"), buttons); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public static List<ResearchTab> getAllTabs(){ return TABS; }

	public static void addNewTab(ResearchTab tab){
		if(TABS.indexOf(tab) < 0){
			TABS.add(tab);
			ResearchTab.addNewTab(tab);
		}
	}

	public List<ResearchButton> getRelatedResearch(){ return new ArrayList(RELATED_RESEARCH); }

	public void addRelatedResearch(ResearchButton r){ if(RELATED_RESEARCH.indexOf(r) < 0) RELATED_RESEARCH.add(r); }

	public String getTabName(){ return TAB_NAME; }

	public GuiLocation getTabBackground(){ return tabBackground == null ? GuiLocation.default_scrollable_background : tabBackground; }

	public void updateButtons(){ buttonsUpdated = true; }

	public boolean isButtonsUpdated(){ return buttonsUpdated; }

	public void init(){ }

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

//<editor-fold defaultstate="collapsed" desc=". . . . JsonWriter Helpers">
	public static List<ResearchTab> getAllKnownInstances(){
		return Arrays.asList(
				new ResearchTab("lava_tab.default_tab",
						"default_tab",
						new GuiLocation(new ResourceLocation(""), new ResourceLocation("lavasources:default_scrollable_background"), 16, 16),
						new ResearchButton(1,1, Research.getAllKnownInstances().get(0), "This is simply a test research", new GuiLocation(new ResourceLocation(""), new ResourceLocation("lavasources:lava_bucket"), 16, 16))
				)
		);
	}

	public static Map<String, JsonWriting.Getter<ResearchTab, JsonElement>> getGetters(){
		Map<String, JsonWriting.Getter<ResearchTab,JsonElement>> getters = new HashMap<>();
		getters.put("name", r -> new JsonPrimitive(r.TAB_NAME));
		getters.put("key", r -> new JsonPrimitive(r.getRegistryName().toString()));
		getters.put("image", r -> new JsonPrimitive(r.tabBackground.getRegistryName().toString()));
		getters.put("research", r ->{
			JsonArray arr = new JsonArray();
			System.out.println(r.RELATED_RESEARCH);
			for(ResearchButton button : r.RELATED_RESEARCH){
				JsonObject obj = new JsonObject();
				obj.add("researchName", new JsonPrimitive(button.getResearch().getName()));
				obj.add("description", new JsonPrimitive(button.getDescription()));
				obj.add("x", new JsonPrimitive(button.getSaveX()));
				obj.add("y", new JsonPrimitive(button.getSaveY()));
				obj.add("image", new JsonPrimitive(button.getDrawImage().getRegistryName().toString()));
				arr.add(obj);
			}
			return arr;
		});

		return getters;
	}
	
	
	public static File getSaveFileLocation(ModContainer c){ return getSaveFileLocation(c.getModId()); }
	public static File getSaveFileLocation(String modId){ return new File(JsonWriting.getModAssetDir(modId) + "tabs.json"); }
	public static File getSaveFileLocationForLavaSources(){ return getSaveFileLocation("lavasources"); }
//</editor-fold>

}
