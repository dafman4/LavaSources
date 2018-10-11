package squedgy.lavasources.research;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;

import java.util.ArrayList;
import java.util.List;

public class Research extends IForgeRegistryEntry.Impl<Research>{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private String description, displayName;
	private final List<Research> DEPENDENCIES = new ArrayList<>();
	private final String[] research_names;
	public final GuiLocation IMAGE;

	public Research(String name, String key, String... dependencies){
		this(name, key, GuiLocation.lava_bucket, dependencies);
	}

	public Research(String name, String key, GuiLocation location, String... dependencies){
		if(key.indexOf(':') < 0)setRegistryName(LavaSources.MOD_ID, key);
		else setRegistryName(key);
		research_names = dependencies;
		IMAGE = location;
		this.displayName = name;
		LavaSources.writeMessage(getClass(), "Instantiated research with name: " + getDisplayName().getUnformattedText() + ", and registry key: "+ getRegistryName());
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public List<Research> getDependencies(){ return new ArrayList(DEPENDENCIES); }
	public String getName(){ return displayName; }
	public ITextComponent getDisplayName(){ return displayName.startsWith("lava_research.") ?  new TextComponentTranslation(displayName) : new TextComponentString(displayName); }
	public void init(){ for(String s : research_names) DEPENDENCIES.add(GameRegistry.findRegistry(Research.class).getValue(new ResourceLocation(s))); }

//</editor-fold>

	@Override
	public String toString() { return "Research{name="+displayName+", dependencies="+this.DEPENDENCIES+"}"; }


	public static void initAllResearch(){
		//change the research strings from file to research classes to allow faster comparisons
		GameRegistry.findRegistry(Research.class).getValuesCollection().forEach(Research::init);
		//afterwards initialize the tabs for similar means
		ResearchTab.initAllTabs();
	}

	public static Research getResearch(ResourceLocation rl){ return CustomRegistryUtil.getRegistryEntry(Research.class, rl); }
	public static Research getResearch(String name){ return CustomRegistryUtil.getRegistryEntry(Research.class, name); }

}
