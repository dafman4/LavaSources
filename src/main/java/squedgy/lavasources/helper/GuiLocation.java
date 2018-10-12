package squedgy.lavasources.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.helper.JsonWriting.Getter;
import squedgy.lavasources.LavaSources;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber
@ObjectHolder(LavaSources.MOD_ID)
public class GuiLocation extends IForgeRegistryEntry.Impl<GuiLocation> {
	public final ResourceLocation location;
	public final int height, width, textureX, textureY;

	@Override
	public String toString() {
		return "GuiLocation{location=" + location + ", height="+height+", width="+width+", textureX="+textureX+", textureY="+textureY+", registryName="+getRegistryName()+"}";
	}

	public GuiLocation(ResourceLocation image, ResourceLocation registry, int width, int height, int textureX, int textureY){
		this.height = height;
		this.width = width;
		this.location = image;
		this.textureX = textureX;
		this.textureY = textureY;
		setRegistryName(registry);
		if(!JsonWriting.writing)
		LavaSources.writeMessage(getClass(), "instantiated GuiLocation" + toString());
	}
	public GuiLocation(ResourceLocation image,ResourceLocation registry, int width, int height){ this(image,registry, width, height, 0 ,0); }
	public static ResourceLocation getResourceLocation(String path){return getRegistryLocation("textures/gui/" + path);}
	public static ResourceLocation getRegistryLocation(String name){ return new ResourceLocation(LavaSources.MOD_ID, name); }

	public static final GuiLocation default_gui = null;
	public static final GuiLocation fillable_vertical = null;
	public static final GuiLocation fillable_horizontal = null;
	public static final GuiLocation default_fill = null;
	public static final GuiLocation energy_fill = null;
	public static final GuiLocation inventory_slot = null;
	public static final GuiLocation fillable_wide = null;
	public static final GuiLocation fillable_wide_overlay = null;
	public static final GuiLocation book_border_corner = null;
	public static final GuiLocation book_border_horizontal = null;
	public static final GuiLocation book_border_vertical = null;
	public static final GuiLocation link_image_border = null;
	public static final GuiLocation book_base = null;
	public static final GuiLocation default_scrollable_background = null;
	public static final GuiLocation lava_bucket = null;
	public static final GuiLocation default_button = null;
	public static final GuiLocation default_button_hover = null;
	public static final GuiLocation default_button_disabled = null;

	public static GuiLocation getGuiLocation(String name){ return CustomRegistryUtil.getRegistryEntry(GuiLocation.class, name); }
	public static GuiLocation getGuiLocation(ResourceLocation rl){ return CustomRegistryUtil.getRegistryEntry(GuiLocation.class, rl); }

//<editor-fold defaultstate="collapsed" desc=". . . . JsonWriting info">
	public static Map<String, Getter<GuiLocation, JsonElement>> getGetters(List<String> keys){
		Map<String, Getter<GuiLocation, JsonElement>> ret = new HashMap();
		ret.put("image", l -> new JsonPrimitive("#"+keys.indexOf(l.location.toString())));
		ret.put("name",  l -> new JsonPrimitive(l.getRegistryName().toString()));
		ret.put("width", l -> new JsonPrimitive(l.width));
		ret.put("height",l -> new JsonPrimitive(l.height));
		ret.put("textureX", l -> new JsonPrimitive(l.textureX));
		ret.put("textureY", l -> new JsonPrimitive(l.textureY));
		return ret;
	}

	public static List<GuiLocation> getAllKnownInstances(){
		return Arrays.asList(
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("default_fill"), 16, 16, 8 , 8),
			new GuiLocation(getResourceLocation("container/blank_default_gui.png"),getRegistryLocation("default_gui"), 176, 132),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("fillable_horizontal"), 26, 8, 7 ,0),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("fillable_vertical"),8, 34),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("energy_fill"), 16, 16, 8 , 24),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("inventory_slot"), 18, 18, 24 , 8),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("fillable_wide"), 62, 36, 0 , 40),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("fillable_wide_overlay"), 60, 34, 0 , 76),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("book_border_corner"), 2, 2, 0, 110),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("book_border_horizontal"), 20, 2, 0, 110),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("book_border_vertical"), 2, 20, 0, 110),
			new GuiLocation(getResourceLocation("elements/elements.png"),getRegistryLocation("link_image_border"), 18, 18, 2, 112),
			new GuiLocation(getResourceLocation("container/book_default.png"),getRegistryLocation("book_base"), 200, 180),
			new GuiLocation(new ResourceLocation("textures/blocks/stone.png"),getRegistryLocation("default_scrollable_background"), 16, 16),
			new GuiLocation(new ResourceLocation("textures/items/bucket_lava.png"),getRegistryLocation("lava_bucket"), 16, 16)
		);
	}

	public static File getSaveFileLocationForLavaSources(){ return getSaveFileLocation("lavasources"); }

	public static File getSaveFileLocation(String modId){ return new File(JsonWriting.getModAssetDir(modId) + "locations.json"); }

	public static File getSaveFileLocation(ModContainer mod){ return getSaveFileLocation(mod.getModId()); }

//</editor-fold>
}
