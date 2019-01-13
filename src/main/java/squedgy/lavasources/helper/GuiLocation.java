package squedgy.lavasources.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.CustomRegistryUtil;
import squedgy.lavasources.LavaSources;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber
public class GuiLocation extends IForgeRegistryEntry.Impl<GuiLocation> {
	public final TextureWrapper texture;
	public final int height, width, textureX, textureY;
	public final float maxU, minU, maxV, minV;

	@Override
	public String toString() {
		return "GuiLocation{texture=" + texture + ", height="+height+", width="+width+", textureX="+textureX+", textureY="+textureY+", registryName="+getRegistryName()+"}";
	}

	public GuiLocation(TextureWrapper image, ResourceLocation registry, int width, int height){
		this(image, registry, width, height, 0, 0);
	}

	public GuiLocation(TextureWrapper image, ResourceLocation registry, int width, int height, int textureX, int textureY){
		this.height = height;
		this.width = width;
		this.texture = image;
		this.textureX = textureX;
		this.textureY = textureY;
		this.minU = ((float)textureX / texture.width);
		this.maxU = ((float)textureX + width)/ texture.width;
		this.minV = ((float)textureY / texture.height);
		this.maxV = ((float)textureY + height) / texture.height;
		setRegistryName(registry);;
	}

	public static ResourceLocation getResourceLocation(String path){return getRegistryLocation("textures/gui/" + path);}
	public static ResourceLocation getRegistryLocation(String name){ return StringUtils.getResourceLocation(LavaSources.MOD_ID, name); }

	@ObjectHolder(LavaSources.MOD_ID)
	public static class TextureWrappers{
		public static final TextureWrapper elements = null;
		public static final TextureWrapper default_gui = null;
		public static final TextureWrapper book_base = null;
		public static final TextureWrapper default_scrollable_background = null;
	}

	@ObjectHolder(LavaSources.MOD_ID)
	public static class GuiLocations {
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
		public static final GuiLocation book_partial = null;
		public static final GuiLocation next_page = null;
		public static final GuiLocation previous_page = null;
		public static final GuiLocation next_page_hover = null;
		public static final GuiLocation previous_page_hover = null;
	}

	public static GuiLocation getGuiLocation(String name){ return CustomRegistryUtil.getRegistryEntry(GuiLocation.class, name); }
	public static GuiLocation getGuiLocation(ResourceLocation rl){ return CustomRegistryUtil.getRegistryEntry(GuiLocation.class, rl); }

	public static class TextureWrapper extends IForgeRegistryEntry.Impl<TextureWrapper>{
		public final ResourceLocation location;
		public final int height, width;

		public TextureWrapper(ResourceLocation location, String registry, int width, int height){
			this(location, StringUtils.getResourceLocation(LavaSources.MOD_ID, registry), width, height);
		}

		public TextureWrapper(ResourceLocation location, ResourceLocation registry, int width, int height){
			setRegistryName(registry);
			this.location =location;
			this.height = height;
			this.width = width;
		}

		@Override
		public String toString() {
			return "TextureWrapper{" +
					"location=" + location +
					", height=" + height +
					", width=" + width +
					'}';
		}
	}

}
