package squedgy.lavasources.init;

import com.google.gson.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@ObjectHolder(LavaSources.MOD_ID)
public class ModResearch {

	public static final ResearchTab DEFAULT_TAB = null;
	public static final Research TEST = null;

	@Mod.EventBusSubscriber
	public static class RegistryHandler{

		public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		public static void register(){  }

//<editor-fold defaultstate="collapsed" desc=". . . . Event Handlers">

		@SubscribeEvent
		public static void registerResearch(RegistryEvent.Register<Research> event){
			Loader.instance().getActiveModList().forEach(mod ->findResearchForMod(mod, event));
		}

		@SubscribeEvent
		public static void registerResearchTabs(RegistryEvent.Register<ResearchTab> event){
			Loader.instance().getActiveModList().forEach(mod ->registerTabsForMod(mod, event));
		}

		@SubscribeEvent
		public static void registerGuiLocations(RegistryEvent.Register<GuiLocation> event){
			LavaSources.writeMessage(GuiLocation.class, "\n\n\n\tRegistering GuiLocations");
			Loader.instance().getActiveModList().forEach(mod -> registerGuiLocationsForMod(mod, event));
		}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . File Reader Methods">

		public static void registerGuiLocationsForMod(ModContainer mod, RegistryEvent.Register<GuiLocation> event){
			JsonContext context = new JsonContext(mod.getModId());

			CraftingHelper.findFiles(
					mod,
					"assets/" + mod.getModId() + "/guilocations",
					root -> true,
					(root, file) ->{
						Loader.instance().setActiveModContainer(mod);
						String relative = root.relativize(file).toString();
						if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) return true;
						String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
						ResourceLocation key = new ResourceLocation(context.getModId(), name);
						BufferedReader reader = null;
						try{
							reader = Files.newBufferedReader(file);
							JsonObject json = JsonUtils.fromJson(ModResearch.RegistryHandler.GSON, reader, JsonObject.class);
							if(!jsonHasAllMembers(json, "locations"))return true;
							event.getRegistry().registerAll(getGuiLocationFromJson(json, context).toArray(new GuiLocation[0]));
						}catch(Exception e){
							LavaSources.writeMessage(GuiLocation.class, "Error reading file " + file + "::: error: " + e);
						}finally{
							IOUtils.closeQuietly(reader);
						}

						return true;
					},
					true,
					true
			);

		}

		public static void findResearchForMod(ModContainer mod, RegistryEvent.Register<Research> event){

			JsonContext context = new JsonContext(mod.getModId());

			CraftingHelper.findFiles(mod,
					"assets/" + mod.getModId() + "/lava_research",
					root -> true,
					(root, file) -> {
						Loader.instance().setActiveModContainer(mod);
						String relative = root.relativize(file).toString();
						if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) return true;
						String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
						ResourceLocation key = new ResourceLocation(context.getModId(), name);
						BufferedReader reader = null;
						try {
							LavaSources.writeMessage(Research.class, "found new file for research creation:" + file);
							reader = Files.newBufferedReader(file);
							JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
							if(json == null) throw new IllegalArgumentException("The given file was not parsable as a json even with the json extension.");
							if(!jsonHasAllMembers(json, "name", "key"))return true;
							event.getRegistry().register(getResearchFromJson(json, context));
							LavaSources.writeMessage(Research.class, "registered");
						}catch(Exception e) {
							LavaSources.writeMessage(Research.class, "Error reading research json at: " + file + "\n\t\t" + e.getLocalizedMessage());
						}finally{
							IOUtils.closeQuietly(reader);
						}

						return true;
					},
					true,
					true
			);

		}

		public static void registerTabsForMod(net.minecraftforge.fml.common.ModContainer mod, RegistryEvent.Register<ResearchTab> event){
			JsonContext context = new JsonContext(mod.getModId());

			CraftingHelper.findFiles(mod,
					"assets/" + mod.getModId() + "/lava_tabs",
					root -> true,
					(root, file) -> {
						Loader.instance().setActiveModContainer(mod);
						String relative = root.relativize(file).toString();
						if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_")) return true;
						String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
						ResourceLocation key = new ResourceLocation(context.getModId(), name);
						BufferedReader reader = null;
						try {
							LavaSources.writeMessage(ResearchTab.class, "found new file for ResearchTabs: " + file);
							reader = Files.newBufferedReader(file);
							JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
							if(json == null) throw new IllegalArgumentException("The given file was not parsable as a json even with the json extension.");
							if(!jsonHasAllMembers(json, "name", "key"))return true;
							event.getRegistry().register(getResearchTabFromJson(json, context));
							LavaSources.writeMessage(ResearchTab.class, "registered");
						}catch(Exception e) {
							LavaSources.writeMessage(ResearchTab.class, "Error reading research tab json at: " + file + "\n\t\t" + e.getLocalizedMessage());
						}finally{
							IOUtils.closeQuietly(reader);
						}

						return true;
					},
					true,
					true
			);

		}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Instantiators">

		public static List<GuiLocation> getGuiLocationFromJson(JsonObject json, JsonContext context){
			Map<String, String> keys = null;
			if(jsonHasAllMembers(json, "keys")){
				keys = json.get("keys").getAsJsonObject().entrySet().stream().collect(Collectors.toMap(Entry::getKey, i -> i.getValue().getAsJsonPrimitive().getAsString()));
			}
			JsonArray locations = JsonUtils.getJsonArray(json, "locations");
			List<GuiLocation> ret = new ArrayList<>(locations.size());
			for(int i = 0; i < locations.size(); i++){
				JsonObject location = locations.get(i).getAsJsonObject();
				if(jsonHasAllMembers(location, "image", "name", "width", "height", "textureX", "textureY")){
					String image = JsonUtils.getString(location, "image"), name = JsonUtils.getString(location, "name");
					int width = JsonUtils.getInt(location, "width"), height = JsonUtils.getInt(location, "height"),
							textureX = JsonUtils.getInt(location, "textureX"), textureY = JsonUtils.getInt(location, "textureY");
					ret.add(
						new GuiLocation(
							new ResourceLocation(image.startsWith("#") ? keys.get(image.substring(1)) : image),
							new ResourceLocation(name.indexOf(':') < 0 ? context.getModId() + ":" + name : name),
							width,
							height,
							textureX,
							textureY
						)
					);
				}else LavaSources.writeMessage(ModResearch.class, "There was an issue turning the following json object into a location: " + location.toString());
			}
			return ret;
		}

		public static Research getResearchFromJson(JsonObject json , JsonContext context){
			LavaSources.writeMessage(Research.class, "research json = " + json.toString());
			String name = JsonUtils.getString(json, "name"), key = JsonUtils.getString(json, "key");
			List<String> research = new ArrayList<>();
			if(json.has("dependencies")){
				JsonArray arr = JsonUtils.getJsonArray(json, "dependencies");
				for(int i = 0; i < arr.size(); i++){
					research.add(arr.get(i).getAsString());
				}
			}
			if(json.has("image")) return new Research(name, key,  GuiLocation.getGuiLocation(JsonUtils.getString(json, "image")), research.toArray(new String[0]));

			return new Research(name, key, research.toArray(new String[0]));
		}

		public static ResearchTab getResearchTabFromJson(JsonObject json , JsonContext context){
			LavaSources.writeMessage(ResearchTab.class, "research tab json = " + json.toString());
			String name = JsonUtils.getString(json, "name"), key = JsonUtils.getString(json, "key");
			List<String> research = new ArrayList<>();
			if(json.has("research")){
				JsonArray arr = JsonUtils.getJsonArray(json, "research");
				for(int i = 0; i < arr.size(); i++){
					JsonObject researchTab = arr.get(i).getAsJsonObject();
					if(jsonHasAllMembers(researchTab,"researchName","x","y","description")){
						String researchName = JsonUtils.getString(researchTab, "researchName"), description = JsonUtils.getString(researchTab, "description");
						int x = JsonUtils.getInt(researchTab, "x"), y = JsonUtils.getInt(researchTab, "y");
					}
				}
			}
			if(json.has("image")) return new ResearchTab(name, key,  GuiLocation.getGuiLocation(JsonUtils.getString(json, "image")), research.toArray(new String[0]));

			return new ResearchTab(name, key, research.toArray(new String[0]));
		}

//</editor-fold>


		public static boolean jsonHasAllMembers(JsonObject json ,String... members){ return Arrays.stream(members).allMatch(json::has); }

	}
}
