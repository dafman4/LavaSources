package squedgy.lavasources.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.crafting.recipes.CoreModifierRecipe;
import squedgy.lavasources.generic.recipes.ICoreModifierRecipe;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.init.ModRegistries;
import squedgy.lavasources.init.ModResearch;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static squedgy.lavasources.init.ModResearch.RegistryHandler.jsonHasAllMembers;
import static squedgy.lavasources.init.ModResearch.RegistryHandler.GSON;

@SuppressWarnings("ConstantConditions")
public class EventListener {

	@SubscribeEvent
	public void onLogIn(PlayerEvent.PlayerLoggedInEvent event){
		StringBuilder message = new StringBuilder();
		EntityPlayer player = event.player;
		if(player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)){
			IPlayerResearchCapability cap = player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
			List<Research> research = cap.getResearch();
			if(research.size() > 0) for(Research r : research) if(r != null) message.append(r.getName()).append(", ");
			else message.append("no research topics");
		}else message.append("no research capabilities.");

		LavaSources.writeMessage(getClass(), "\n\n\nplayer" + player.getName() + " logged in with " + message + "\n\n\n");
	}

	@SubscribeEvent
	public void registryRegister(RegistryEvent.NewRegistry event){
		LavaSources.writeMessage(getClass(), "\n\n\n\tregistering registries.");
		ModRegistries.RESEARCH_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "b_research_registry")).setType(Research.class).disableSaving().create();
		ModRegistries.GUI_LOCATION_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "a_gui_location_registry")).setType(GuiLocation.class).disableSaving().create();
		ModRegistries.RESEARCH_TAB_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "c_research_tab_registry")).setType(ResearchTab.class).disableSaving().create();
		ModRegistries.CORE_MODIFIER_RECIPE_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "core_modifier_recipe_registry")).setType(ICoreModifierRecipe.class).disableSaving().create();
	}

	public static String getLavasourcesBaseForMod(ModContainer mod){
		return "assets/" + mod.getModId() + "/lavasources_saves/";
	}

//<editor-fold defaultstate="collapsed" desc=". . . . GuiLocations">

	@SubscribeEvent
	public void registerGuiLocations(RegistryEvent.Register<GuiLocation> event){
		LavaSources.writeMessage(GuiLocation.class, "\n\n\n\tRegistering GuiLocations");
		Loader.instance().getActiveModList().forEach(mod -> registerGuiLocationsForMod(mod, event));
	}

	public void registerGuiLocationsForMod(ModContainer mod, RegistryEvent.Register<GuiLocation> event){
		JsonContext context = new JsonContext(mod.getModId());
		CraftingHelper.findFiles(
				mod,
				"assets/" + mod.getModId() + "/lavasources_saves/locations.json",
				root -> root.endsWith("locations.json"),
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
		Map<String, String> keys = null;
		if(jsonHasAllMembers(json, "keys")){
			keys = json.get("keys").getAsJsonObject().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().getAsJsonPrimitive().getAsString()));
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

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . CoreModifierRecipes">

	@SubscribeEvent
	public void registerCoreModifierRecipes(RegistryEvent.Register<ICoreModifierRecipe> registry){
		LavaSources.writeMessage(ICoreModifierRecipe.class, "\n\n\n\tRegistering ICoreModifierRecipes");
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
					ResourceLocation key = new ResourceLocation(context.getModId(), name);

					BufferedReader reader = null;
					try{
						reader = Files.newBufferedReader(file);
						JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
						if(jsonHasAllMembers(json, "recipes")){
							registry.getRegistry().registerAll(getModifierRecipesFromJson(json, context).toArray(new ICoreModifierRecipe[0]));
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

	private List<ICoreModifierRecipe> getModifierRecipesFromJson(JsonObject json, JsonContext context){
		JsonArray a = JsonUtils.getJsonArray(json, "recipes");
		List<ICoreModifierRecipe> ret = new ArrayList<>();
		for(int i = 0; i < a.size(); i++){
			JsonObject recipe = a.get(i).getAsJsonObject();
			String fluidName = JsonUtils.getString(recipe, "fluid");
			ItemStack output = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(JsonUtils.getString(recipe, "output"))).getDefaultInstance();
			int requiredEnergy = JsonUtils.getInt(recipe, "energy", 0), fluidAmount = JsonUtils.getInt(recipe, "fluidAmount", 0);
			String name = JsonUtils.getString(recipe, "name");
			if(FluidRegistry.isFluidRegistered(fluidName)){
				ret.add(new CoreModifierRecipe(new ResourceLocation(name.indexOf(':') < 0 ? LavaSources.MOD_ID + ":" + name : name), output, FluidRegistry.getFluidStack(fluidName, fluidAmount), requiredEnergy));
			}else throw new IllegalArgumentException("There was an issue with an unregistered fluid: " + fluidName);
		}

		return ret;
	}

//</editor-fold>

}
