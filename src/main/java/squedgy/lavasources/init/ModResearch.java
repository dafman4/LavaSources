package squedgy.lavasources.init;

import com.google.gson.*;
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
import squedgy.lavasources.research.ResearchButton;

import java.io.BufferedReader;
import java.io.File;
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
			LavaSources.writeMessage(GuiLocation.class, "\n\n\n\tRegistering Research");
			Loader.instance().getActiveModList().forEach(mod -> registerResearchForMod(mod, event));
		}

		@SubscribeEvent
		public static void registerResearchTabs(RegistryEvent.Register<ResearchTab> event){
			LavaSources.writeMessage(GuiLocation.class, "\n\n\n\tRegistering ResearchTabs");
			Loader.instance().getActiveModList().forEach(mod ->registerTabsForMod(mod, event));
		}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . File Reader Methods">

		public static void registerResearchForMod(ModContainer mod, RegistryEvent.Register<Research> event){

			JsonContext context = new JsonContext(mod.getModId());
			CraftingHelper.findFiles(
					mod,
					"assets/" + mod.getModId() + "/lavasources_saves/research.json",
					root -> root.endsWith("research.json"),
					(root, file) ->{
						Loader.instance().setActiveModContainer(mod);

						String relative = root.relativize(file).toString();
						if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
							return true;
						String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
						ResourceLocation key = new ResourceLocation(context.getModId(), name);

						BufferedReader reader = null;
						try{
							reader = Files.newBufferedReader(file);
							JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
							if(jsonHasAllMembers(json, "research")){
								event.getRegistry().registerAll(getResearchFromJson(json, context).toArray(new Research[0]));
								LavaSources.writeMessage(Research.class, "registered");
							}
						}catch(Exception e){
							LavaSources.writeMessage(Research.class, "Error reading file " + file + "::: error: " + e);
						}finally {IOUtils.closeQuietly(reader);}
						return true;
					},
					true,
					true
			);

		}

		public static void registerTabsForMod(net.minecraftforge.fml.common.ModContainer mod, RegistryEvent.Register<ResearchTab> event){
			JsonContext context = new JsonContext(mod.getModId());
			CraftingHelper.findFiles(
					mod,
					"assets/" + mod.getModId() + "/lavasources_saves/tabs.json",
					root -> root.endsWith("tabs.json"),
					(root, file) ->{
						Loader.instance().setActiveModContainer(mod);

						String relative = root.relativize(file).toString();
						if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
							return true;
						String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
						ResourceLocation key = new ResourceLocation(context.getModId(), name);

						BufferedReader reader = null;
						try{
							reader = Files.newBufferedReader(file);
							JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
							if(jsonHasAllMembers(json, "tabs")){
								event.getRegistry().registerAll(getResearchTabFromJson(json, context).toArray(new ResearchTab[0]));
								LavaSources.writeMessage(ResearchTab.class, "registered");
							}
						}catch(Exception e){
							LavaSources.writeMessage(ResearchTab.class, "Error reading file " + file + "::: error: " + e);
						}finally {IOUtils.closeQuietly(reader);}
						return true;
					},
					true,
					true
			);
		}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Instantiators">

		public static List<Research> getResearchFromJson(JsonObject json , JsonContext context){
			JsonArray locations = JsonUtils.getJsonArray(json, "research");
			List<Research> ret = new ArrayList<>(locations.size());
			for(int i = 0; i < locations.size(); i++){
				JsonObject location = locations.get(i).getAsJsonObject();
				if(jsonHasAllMembers(location, "name", "key", "dependencies")){
					String name = JsonUtils.getString(location, "name"),
							key = JsonUtils.getString(location, "key");
					JsonArray dependencies = JsonUtils.getJsonArray(location, "dependencies");
					String[] depends = new String[dependencies.size()];
					for(int f = 0; f < dependencies.size(); f++) depends[f] = dependencies.get(f).getAsJsonPrimitive().getAsString();
					ret.add(
							new Research(name, key, depends)
					);
				}else LavaSources.writeMessage(ModResearch.class, "There was an issue turning the following json object into a location: " + location.toString());
			}
			return ret;
		}

		public static List<ResearchTab> getResearchTabFromJson(JsonObject json , JsonContext context){
			JsonArray locations = JsonUtils.getJsonArray(json, "tabs");
			List<ResearchTab> ret = new ArrayList<>(locations.size());
			for(int i = 0; i < locations.size(); i++){
				JsonObject location = locations.get(i).getAsJsonObject();
				if(jsonHasAllMembers(location, "image", "name", "key", "research")){
					String image = JsonUtils.getString(location, "image"),
							name = JsonUtils.getString(location, "name"),
							key = JsonUtils.getString(location, "key");
					JsonArray dependencies = JsonUtils.getJsonArray(location, "research");
					ResearchButton[] depends = new ResearchButton[dependencies.size()];
					for(int f = 0; f < dependencies.size(); f++) depends[f] = getResearchButtonFromJsonObject(dependencies.get(i).getAsJsonObject());
					ret.add(
							new ResearchTab(name, key, depends)
					);
				}else LavaSources.writeMessage(ModResearch.class, "There was an issue turning the following json object into a location: " + location.toString());
			}
			return ret;
		}

		public static ResearchButton getResearchButtonFromJsonObject(JsonObject json){
			if(jsonHasAllMembers(json, "researchName", "x", "y", "description", "image")){
				String name = JsonUtils.getString(json, "researchName"),
						description = JsonUtils.getString(json ,"description"),
						image = JsonUtils.getString(json, "image");
				int x = JsonUtils.getInt(json, "x"), y = JsonUtils.getInt(json, "y");
				return new ResearchButton(x, y, Research.getResearch(name), description, GuiLocation.getGuiLocation(image));
			}else
				throw new IllegalArgumentException("The given json object did not have all required members \"researchName, x, y, description, image\": " + json);
		}

//</editor-fold>


		public static boolean jsonHasAllMembers(JsonObject json ,String... members){ return Arrays.stream(members).allMatch(json::has); }

	}
}
