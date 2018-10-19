package squedgy.lavasources.research;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class Research extends IForgeRegistryEntry.Impl<Research>{

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	private String displayName;
	private final List<Research> DEPENDENCIES = new ArrayList<>();
	private final String[] research_names;

	public Research(String name, String key, String... dependencies){
		if(key.indexOf(':') < 0)setRegistryName(LavaSources.MOD_ID, key);
		else setRegistryName(key);
		research_names = dependencies;
		this.displayName = name;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public List<Research> getDependencies(){ return new ArrayList(DEPENDENCIES); }
	public String getName(){ return displayName; }
	public ITextComponent getDisplayName(){ return displayName.startsWith("lava_research.") ?  new TextComponentTranslation(displayName) : new TextComponentString(displayName); }
	public void init(){ for(String s : research_names) DEPENDENCIES.add(getResearch(s)); }

//</editor-fold>


	@Override
	public String toString() {
		return "Research{" +
				", displayName='" + displayName + '\'' +
				", DEPENDENCIES=" + DEPENDENCIES + '\'' +
				", RegistryName='" + getRegistryName() + '\'' +
		'}';
	}

	public static void initAllResearch(){
		//change the research strings from file to research classes to allow faster comparisons
		GameRegistry.findRegistry(Research.class).getValuesCollection().forEach(Research::init);
		//afterwards initialize the tabs for similar means
		ResearchTab.initAllTabs();
	}

	public static Research getResearch(ResourceLocation rl){ return CustomRegistryUtil.getRegistryEntry(Research.class, rl); }
	public static Research getResearch(String name){ return CustomRegistryUtil.getRegistryEntry(Research.class, name); }

}
