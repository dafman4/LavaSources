package squedgy.lavasources.helper;

import com.google.gson.*;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.research.Research;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonWriting {

	public static boolean writing = false;

	public static void main(String[] args){
		writing = true;
//		List<GuiLocation> guiLocations = GuiLocation.getAllKnownInstances();
//		List<String> keys = (guiLocations).stream().map(l -> l.location.toString()).distinct().collect(Collectors.toList());
//		JsonObject jsonStrings = new JsonObject();
//		for(int i = 0; i < keys.size(); i++) jsonStrings.add(""+i, new JsonPrimitive(keys.get(i)));
//		Map<String, JsonObject> prepends = new HashMap<>();
//		prepends.put("keys", jsonStrings);
//		writeObjectsToJsonFile(guiLocations, GuiLocation.getSaveFileLocationForLavaSources(), GuiLocation.getGetters(keys), prepends, "locations");

//		List<Research> research = Research.getAllKnownInstances();
//		Map<String, JsonObject> prepends = new HashMap<>();
//		writeObjectsToJsonFile(research, Research.getSaveFileLocationForLavaSources(), Research.getGetters(), prepends, "research");

//		List<ResearchTab> researchTabs = ResearchTab.getAllKnownInstances();
//		Map<String, JsonObject> prepends = new HashMap<>();
//		writeObjectsToJsonFile(researchTabs, ResearchTab.getSaveFileLocationForLavaSources(), ResearchTab.getGetters(), prepends, "tabs");
	}

	public static <T> void writeObjectsToJsonFile(List<T> objects, File toWrite, Map<String,Getter<T,JsonElement>> property, Map<String, JsonObject> prepends, String listName){
		boolean flag = toWrite.exists();
		//if it don't exist try to make it :3
		if(!flag) {
			try {
				Path parent = toWrite.toPath().getParent();
				flag = parent.toFile().exists() || parent.toFile().mkdirs();
				if(flag && !toWrite.createNewFile()){
					flag = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// if it does exist or was made successfully
		if(flag && (toWrite.canWrite() || toWrite.setWritable(true))) {
			//grand poo-bah object
			JsonObject parent = new JsonObject();
			//take care of the prepend situation
			for (String s : prepends.keySet()) parent.add(s, prepends.get(s));
			//make an array of our object using the getters provided
			JsonArray jsonArray = new JsonArray();
			for (T obj : objects) {
				JsonObject jsonObj = new JsonObject();
				for (String s : property.keySet())
					if (property.get(s).shouldGet(obj)) jsonObj.add(s, property.get(s).get(obj));
				jsonArray.add(jsonObj);
			}
			//add dat bish
			parent.add(listName, jsonArray);
			//write the entire thing
			try (Writer writer = new FileWriter(toWrite)) {
				Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
				gson.toJson(parent, writer);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	public interface Getter<T,L>{
		public abstract L get(T obj);
		public default boolean shouldGet(T obj){ return get(obj) != null; }
	}

	public static String getModAssetDir(String modId){
		File f = new File(JsonWriting.class.getProtectionDomain().getCodeSource().getLocation().getFile());
		while(!f.getName().equals("squedgy")){
			f = f.toPath().getParent().toFile();
			System.out.println(f);
		}
		f = f.toPath().getParent().toFile();
		return f +"/assets/" + modId + "/lavasources_saves/";
	}

}
