package squedgy.lavasources.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.crafting.recipes.CoreModifierRecipe;
import squedgy.lavasources.crafting.recipes.LiquefierRecipe;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;
import squedgy.lavasources.gui.elements.BookDisplayPartial;
import squedgy.lavasources.helper.StringUtils;
import squedgy.lavasources.init.ModResearch;
import squedgy.lavasources.research.ResearchButton;
import squedgy.lavasources.research.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.init.ModRegistries;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;
import squedgy.lavasources.tileentity.TileEntityLiquefier;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.*;

import static squedgy.lavasources.init.ModResearch.RegistryHandler.GSON;

@SuppressWarnings("ConstantConditions")
public class EventListener {

	@SubscribeEvent
	public void onLogIn(PlayerEvent.PlayerLoggedInEvent event){
		StringBuilder message = new StringBuilder("player ");
		EntityPlayer player = event.player;
		message.append(player.getName());
		message.append(" logged in with ");
		if(player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)){
			IPlayerResearchCapability cap = player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
			List<Research> research = cap.getResearch();
			if(research.size() > 0) for(Research r : research){
				if(r != null)message.append(r.getName()).append(", ");
			} else message.append("no research topics");
		}else message.append("no research capabilities.");

		LavaSources.writeMessage(getClass(), message.toString());
	}

	@SubscribeEvent
	public void registryRegister(RegistryEvent.NewRegistry event){
		ModRegistries.TEXTURE_WRAPPER_REGISTRY = createRegistry(GuiLocation.TextureWrapper.class, "a_texture_wrapper_registry");
		ModRegistries.GUI_LOCATION_REGISTRY = createRegistry( GuiLocation.class, "b_gui_location_registry");
		ModRegistries.RESEARCH_REGISTRY = createRegistry(Research.class,  "c_research_registry");
		ModRegistries.RESEARCH_TAB_REGISTRY = createRegistry(ResearchTab.class, "d_research_tab_registry");
		ModRegistries.CORE_MODIFIER_RECIPE_REGISTRY = createRegistry(ICoreModifierRecipe.class, "core_modifier_recipe_registry");
		ModRegistries.LIQUEFIER_RECIPE_REGISTRY = createRegistry(ILiquefierRecipe.class , "liquefier_recipe_registry");
	}

	private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistry(Class<T> clazz, String name){
		return new RegistryBuilder().setType(clazz).setName(LavaSources.getResourceLocation(name)).disableSaving().create();
	}

	public static String getLavasourcesBaseForMod(ModContainer mod){
		return "assets/" + mod.getModId() + "/lavasources_saves/";
	}

	public static boolean jsonHasAllMembers(JsonObject json, String... members){
		return Arrays.stream(members).allMatch(json::has);
	}

//<editor-fold defaultstate="collapsed" desc=". . . . TextureWrappers">
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerTextureWrapper(RegistryEvent.Register<GuiLocation.TextureWrapper> event){
		LavaSources.writeMessage(GuiLocation.TextureWrapper.class, "\nRegistering TextureWrappers");
		Loader.instance().getActiveModList().forEach(m -> registerTextureWrappersForMod(m, event));
	}

	public void registerTextureWrappersForMod(ModContainer mod, RegistryEvent.Register<GuiLocation.TextureWrapper> registry){
		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(mod,
				getLavasourcesBaseForMod(mod) + "locations.json",
				root -> root.endsWith("locations.json"),
				(root, file) ->{
					Loader.instance().setActiveModContainer(mod);

					String relative = root.relativize(file).toString();
					if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "wrappers")){
							registry.getRegistry().registerAll(getTextureWrappersFromJson(json, context));
							LavaSources.writeMessage(GuiLocation.TextureWrapper.class, "registered");
						}
					}catch(Exception e){
						LavaSources.writeMessage(GuiLocation.TextureWrapper.class, "Error reading file " + file + "::: error: " + e);
					}finally {
						IOUtils.closeQuietly(reader);}
					return true;
				},
				true,
				true
				);
	}

	public GuiLocation.TextureWrapper[] getTextureWrappersFromJson(JsonObject json, JsonContext context){
		return json.get("wrappers").getAsJsonObject().entrySet().stream().map(e ->{
			JsonObject obj = e.getValue().getAsJsonObject();
			obj.add("name", new JsonPrimitive(e.getKey()));
			return obj;
		}).map(obj -> {
			if(jsonHasAllMembers(obj, "image", "width", "height")){
				String image = JsonUtils.getString(obj, "image");
				int width = JsonUtils.getInt(obj, "width"),
						height = JsonUtils.getInt(obj, "height");
				return new GuiLocation.TextureWrapper(LavaSources.getResourceLocation(image), JsonUtils.getString(obj, "name"), width, height);
			}else throw new IllegalArgumentException("a TextureWrapper json object for " + context.getModId() + " is missing a required field!");
		}).toArray(GuiLocation.TextureWrapper[]::new);
	}



//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . GuiLocations">

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerGuiLocations(RegistryEvent.Register<GuiLocation> event){
		LavaSources.writeMessage(GuiLocation.class, "\nRegistering GuiLocations");
		Loader.instance().getActiveModList().forEach(mod -> registerGuiLocationsForMod(mod, event));
	}

	public void registerGuiLocationsForMod(ModContainer mod, RegistryEvent.Register<GuiLocation> event){
		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(
				mod,
				getLavasourcesBaseForMod(mod) + "locations.json",
				root -> root.endsWith("locations.json"),
				(root, file) ->{
					Loader.instance().setActiveModContainer(mod);

					String relative = root.relativize(file).toString();
					if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "locations")){
							event.getRegistry().registerAll(getGuiLocationFromJson(json, context).toArray(new GuiLocation[0]));
							LavaSources.writeMessage(GuiLocation.class, "registered");
						}
					}catch(Exception e){
						LavaSources.writeMessage(GuiLocation.class, "Error reading file " + file + "::: error: " + e);
					}finally {
						IOUtils.closeQuietly(reader);}
					return true;
				},
				true,
				true
		);

	}

	public List<GuiLocation> getGuiLocationFromJson(JsonObject json, JsonContext context){
		JsonArray locations = JsonUtils.getJsonArray(json, "locations");
		List<GuiLocation> ret = new ArrayList<>(locations.size());
		for(int i = 0; i < locations.size(); i++){
			JsonObject location = locations.get(i).getAsJsonObject();
			if(jsonHasAllMembers(location, "image", "name", "width", "height", "textureX", "textureY")){
				String image = JsonUtils.getString(location, "image"), name = JsonUtils.getString(location, "name");
				int width = JsonUtils.getInt(location, "width"), height = JsonUtils.getInt(location, "height"),
						textureX = JsonUtils.getInt(location, "textureX"), textureY = JsonUtils.getInt(location, "textureY");
				GuiLocation.TextureWrapper wrapper = CustomRegistryUtil.getRegistryEntry(GuiLocation.TextureWrapper.class, image.substring(1));
				if(wrapper == null) throw new IllegalArgumentException("The provided wrapper " + image + " number did not link to an existing wrapper");
				ret.add(
						new GuiLocation(
								wrapper,
								StringUtils.getResourceLocation(context, name),
								width,
								height,
								textureX,
								textureY
						)
				);
			}else LavaSources.writeMessage(GuiLocation.class, "There was an issue turning the following json object into a location: " + location.toString());
		}
		return ret;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . CoreModifierRecipes">

	@SubscribeEvent
	public void registerCoreModifierRecipes(RegistryEvent.Register<ICoreModifierRecipe> registry){
		LavaSources.writeMessage(ICoreModifierRecipe.class, "\nRegistering ICoreModifierRecipes");
		Loader.instance().getActiveModList().forEach(mod -> registerCoreModifierLocationsForMod(mod, registry));
		TileEntityCoreModifier.initRecipes(GameRegistry.findRegistry(ICoreModifierRecipe.class));
	}

	public void registerCoreModifierLocationsForMod(ModContainer mod, RegistryEvent.Register<ICoreModifierRecipe> registry){
		JsonContext context = new JsonContext(mod.getModId());

		CraftingHelper.findFiles(mod,getLavasourcesBaseForMod(mod) + "modifier.json",
				root -> root.endsWith("modifier.json"),
				(root, file) -> {
					Loader.instance().setActiveModContainer(mod);

					String relative = root.relativize(file).toString();
					if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "recipes")){
							registry.getRegistry().registerAll(getModifierRecipesFromJson(json, context).toArray(new ICoreModifierRecipe[0]));
							LavaSources.writeMessage(ICoreModifierRecipe.class, "registered");
						}
					}catch(Exception e){
						LavaSources.writeMessage(ICoreModifierRecipe.class, "Error reading file " + file + "::: error: " + e);
					}finally {
						IOUtils.closeQuietly(reader);}
					return true;
				},
				true,
				true
		);
	}

	private List<ICoreModifierRecipe> getModifierRecipesFromJson(JsonObject json, JsonContext context){
		JsonArray a = JsonUtils.getJsonArray(json, "recipes");
		List<ICoreModifierRecipe> ret = new ArrayList<>();
		for(int i = 0; i < a.size(); i++){
			JsonObject recipe = a.get(i).getAsJsonObject();
			String fluidName = JsonUtils.getString(recipe, "fluid");
			ItemStack output = GameRegistry.findRegistry(Item.class).getValue(StringUtils.getResourceLocation(context, JsonUtils.getString(recipe, "output"))).getDefaultInstance();
			int requiredEnergy = JsonUtils.getInt(recipe, "energy", 0), fluidAmount = JsonUtils.getInt(recipe, "fluidAmount", 0);
			String name = JsonUtils.getString(recipe, "name");
			if(FluidRegistry.isFluidRegistered(fluidName)){
				ret.add(new CoreModifierRecipe(StringUtils.getResourceLocation(context, name), output, FluidRegistry.getFluidStack(fluidName, fluidAmount), requiredEnergy));
			}else throw new IllegalArgumentException("There was an issue with an unregistered fluid: " + fluidName);
		}

		return ret;
	}

//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . LiquefierRecipes">

	@SubscribeEvent
	public void registerILiquefierRecipe(RegistryEvent.Register<ILiquefierRecipe> event){
		LavaSources.writeMessage(ILiquefierRecipe.class, "\nRegistering ILiquefierRecipe");
		Loader.instance().getActiveModList().forEach(mod -> registerILiquefierRecipeForMod(mod, event));
		TileEntityLiquefier.updateRecipes();
	}

	public void registerILiquefierRecipeForMod(ModContainer mod, RegistryEvent.Register<ILiquefierRecipe> event){
		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(
				mod,
				getLavasourcesBaseForMod(mod) + "liquefier.json",
				root -> root.endsWith("liquefier.json"),
				(root, file) ->{
					Loader.instance().setActiveModContainer(mod);

					String relative = root.relativize(file).toString();
					if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
						return true;
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "recipes")){
							event.getRegistry().registerAll(getLiquefierRecipesFromJson(json, context).toArray(new ILiquefierRecipe[0]));
							LavaSources.writeMessage(ILiquefierRecipe.class, "registered");
						}
					}catch(Exception e){
						LavaSources.writeMessage(ILiquefierRecipe.class, "Error reading file " + file + "::: error: " + e);
					}finally {
						IOUtils.closeQuietly(reader);}
					return true;
				},
				true,
				true
		);

	}

	public List<ILiquefierRecipe> getLiquefierRecipesFromJson(JsonObject json, JsonContext context){
		JsonArray recipes = JsonUtils.getJsonArray(json, "recipes");
		List<ILiquefierRecipe> ret = new ArrayList<>(recipes.size());
		for(int i = 0; i < recipes.size(); i++){
			JsonObject recipe = recipes.get(i).getAsJsonObject();
			if(jsonHasAllMembers(recipe, "name", "fluid", "ore", "output")){
			String name = JsonUtils.getString(recipe, "name"),
					fluid = JsonUtils.getString(recipe, "fluid"),
					ore = JsonUtils.getString(recipe, "ore");
			int output = JsonUtils.getInt(recipe, "output");
			if(OreDictionary.getOres(ore).size() > 0 && FluidRegistry.isFluidRegistered(fluid)){
				ret.add(new LiquefierRecipe(name, FluidRegistry.getFluidStack(fluid, 0), new OreIngredient(ore), output));
			}
			}else LavaSources.writeMessage(ILiquefierRecipe.class, "There was an issue turning the following json object into a location: "
					+ recipe.toString());
		}
		return ret;
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Research">

	@SubscribeEvent
	public void registerResearch(RegistryEvent.Register<Research> event){
		LavaSources.writeMessage(Research.class, "\nRegistering Research");
		try {
			Loader.instance().getActiveModList().forEach(mod -> registerResearchForMod(mod, event));
		}catch(Exception e){
			LavaSources.writeMessage(getClass(),e.getMessage());
		}
	}

	public void registerResearchForMod(ModContainer mod, RegistryEvent.Register<Research> event){

		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(
				mod,
				getLavasourcesBaseForMod(mod) + "research.json",
				root -> root.endsWith("research.json"),
				(root, file) ->{
					Loader.instance().setActiveModContainer(mod);

					String name = FilenameUtils.removeExtension(root.relativize(file).toString()).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

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

	public List<Research> getResearchFromJson(JsonObject json , JsonContext context){
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
						new Research(name, StringUtils.getResourceLocation(context,key), depends)
				);
			}else LavaSources.writeMessage(ModResearch.class, "There was an issue turning the following json object into a location: " + location.toString());
		}
		return ret;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ResearchTabs">

	//tabs and related items are only necessary for the client side as it formats the guide book
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerResearchTabs(RegistryEvent.Register<ResearchTab> event){
		LavaSources.writeMessage(GuiLocation.class, "\nRegistering ResearchTabs");
		Loader.instance().getActiveModList().forEach(mod ->registerTabsForMod(mod, event));
	}

	public void registerTabsForMod(net.minecraftforge.fml.common.ModContainer mod, RegistryEvent.Register<ResearchTab> event){
		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(
				mod,
				"assets/" + mod.getModId() + "/lavasources_saves/tabs.json",
				root -> root.endsWith("tabs.json"),
				(root, file) ->{
					Loader.instance().setActiveModContainer(mod);

					String relative = root.relativize(file).toString();
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "tabs")){
							event.getRegistry().registerAll(getResearchTabFromJson(json, mod, context).toArray(new ResearchTab[0]));
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

	public List<ResearchTab> getResearchTabFromJson(JsonObject json, ModContainer mod,  JsonContext context){
		JsonArray locations = JsonUtils.getJsonArray(json, "tabs");
		List<ResearchTab> ret = new ArrayList<>(locations.size());
		for(int i = 0; i < locations.size(); i++){
			JsonObject location = locations.get(i).getAsJsonObject();
			if(jsonHasAllMembers(location, "image", "name", "key", "research", "height", "width")){
				String image = JsonUtils.getString(location, "image"),
						name = JsonUtils.getString(location, "name"),
						key = JsonUtils.getString(location, "key");
				int height = JsonUtils.getInt(location, "height"),
						width = JsonUtils.getInt(location, "width");
				JsonArray dependencies = JsonUtils.getJsonArray(location, "research");
				ResearchButton[] depends = new ResearchButton[dependencies.size()];
				for(int f = 0; f < dependencies.size(); f++) depends[f] = getResearchButtonFromJsonObject(dependencies.get(f).getAsJsonObject(), mod, context);
				ret.add(
						new ResearchTab(name, width, height, StringUtils.getResourceLocation(context,key), depends)
				);
			}else LavaSources.writeMessage(ModResearch.class, "There was an issue turning the following json object into a location: " + location.toString());
		}
		return ret;
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ResearchButtons">

	public ResearchButton getResearchButtonFromJsonObject(JsonObject json, ModContainer mod, JsonContext context){
		if(jsonHasAllMembers(json, "researchName", "x", "y", "description", "image", "page")){
			String name = JsonUtils.getString(json, "researchName"),
					description = JsonUtils.getString(json ,"description"),
					image = JsonUtils.getString(json, "image"), disabled = null;
			int x = JsonUtils.getInt(json, "x"), y = JsonUtils.getInt(json, "y");
			if(json.has("disabled")){
				disabled = JsonUtils.getString(json, "disabled");
			}
			return new ResearchButton(null, x, y, Research.getResearch(name), description, GuiLocation.getGuiLocation(StringUtils.getResourceLocation(mod, image)), GuiLocation.getGuiLocation(StringUtils.getResourceLocation(mod, disabled == null ? image : disabled)), getPagesFromString(mod, JsonUtils.getString(json, "page"), context));
		}else
			throw new IllegalArgumentException("The given json object did not have all required members \"researchName, x, y, description, image\": " + json);
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Pages">

	public List<Map.Entry<String,String>> getPagesFromString(ModContainer mod, String location, JsonContext context){
		List<Map.Entry<String,String>> partials = new ArrayList<>();
		if(!location.endsWith(".json")) throw new IllegalArgumentException("The provided page location " + location + " is not a JSON file.");
		CraftingHelper.findFiles(
				mod,
				"assets/" + mod.getModId() + "/lavasources_saves/pages/" + location,
				root -> root.endsWith(location),
				(root, file)->{
					Loader.instance().setActiveModContainer(mod);
					String relative = root.relativize(file).toString();
					String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\","/");
					ResourceLocation key = StringUtils.getResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "page")){
							partials.addAll(getElementsFromJson(json, context));
							LavaSources.writeMessage(BookDisplayPartial.class, "registered");
						}
					}catch(Exception e){
						LavaSources.writeMessage(BookDisplayPartial.class, "Error reading file " + file + "::: error: " + e);
					}finally {IOUtils.closeQuietly(reader);}


					return true;
				},
				true,
				false
		);
		return partials;
	}

	public static List<Map.Entry<String, String>> getElementsFromJson(JsonObject json, JsonContext context){
		List<Map.Entry<String, String>> ret = new ArrayList<>();
		int text = 0, image = 0;
		JsonArray array = JsonUtils.getJsonArray(json, "page");
		for (int i = 0; i < array.size(); i++) {
			JsonObject o = array.get(i).getAsJsonObject();
			new AbstractMap.SimpleImmutableEntry<>("", "");
			if(o.has("text")) ret.add(new AbstractMap.SimpleImmutableEntry<>("text", JsonUtils.getString(o, "text")));
			else if(o.has("image")) ret.add(new AbstractMap.SimpleImmutableEntry<>("image", JsonUtils.getString(o, "image")));
		}
		return ret;
	}

//</editor-fold>

}
