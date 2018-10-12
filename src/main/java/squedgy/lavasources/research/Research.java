package squedgy.lavasources.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.helper.JsonWriting;
import squedgy.lavasources.helper.JsonWriting.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
		if(!JsonWriting.writing)
		LavaSources.writeMessage(getClass(), "Instantiated research: " + toString());
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

	public List<Research> getDependencies(){ return new ArrayList(DEPENDENCIES); }
	public String getName(){ return displayName; }
	public ITextComponent getDisplayName(){ return displayName.startsWith("lava_research.") ?  new TextComponentTranslation(displayName) : new TextComponentString(displayName); }
	public void init(){ for(String s : research_names) DEPENDENCIES.add(GameRegistry.findRegistry(Research.class).getValue(new ResourceLocation(s))); }

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

//<editor-fold defaultstate="collapsed" desc=". . . . JsonWriting info">

	public static List<Research> getAllKnownInstances(){
		return Arrays.asList(
			new Research("test", "test")
		);
	}

	public static Map<String, Getter<Research, JsonElement>> getGetters(){
		Map<String, Getter<Research,JsonElement>> getters = new HashMap<>();
		getters.put("name", r -> new JsonPrimitive(r.displayName));
		getters.put("key", r -> new JsonPrimitive(r.getRegistryName().toString()));
		getters.put("dependencies", r ->{
			JsonArray arr = new JsonArray();
			for(Research research : r.DEPENDENCIES) arr.add(new JsonPrimitive(research.getRegistryName().toString()));
			return arr;
		});

		return getters;
	}

	public static File getSaveFileLocationForLavaSources(){ return getSaveFileLocation("lavasources"); }

	public static File getSaveFileLocation(String modId){
		return new File(JsonWriting.getModAssetDir(modId) + "research.json");
	}

	public static File getSaveFileLocation(ModContainer mod){ return getSaveFileLocation(mod.getModId()); }

//</editor-fold>

}
