package squedgy.lavasources.helper;

import net.minecraft.util.ResourceLocation;
import squedgy.lavasources.LavaSources;

public class GuiLocation {
	public final ResourceLocation location;
	public final int height, width, textureX, textureY;

	private GuiLocation(ResourceLocation location, int width, int height, int textureX, int textureY){
		this.height = height;
		this.width = width;
		this.location = location;
		this.textureX = textureX;
		this.textureY = textureY;
	}
	private GuiLocation(ResourceLocation location, int width, int height){
		this(location, width, height, 0 ,0);
	}
	private static ResourceLocation getResourceLocation(String path){return new ResourceLocation(LavaSources.MOD_ID, "textures/gui/" + path);}

	public static GuiLocation DEFAULT_GUI = new GuiLocation(getResourceLocation("container/blank_default_gui.png"), 176, 132);
	public static GuiLocation FILLABLE_VERTICAL = new GuiLocation(getResourceLocation("elements/elements.png"),8, 36);
	public static GuiLocation FILLABLE_HORIZONTAL = new GuiLocation(getResourceLocation("elements/elements.png"), 26, 8, 7 ,0);
	public static GuiLocation DEFAULT_FILL = new GuiLocation(getResourceLocation("elements/elements.png"), 16, 16, 8 , 8);
	public static GuiLocation ENERGY_FILL = new GuiLocation(getResourceLocation("elements/elements.png"), 16, 16, 8 , 24);
	public static GuiLocation INVENTORY_SLOT = new GuiLocation(getResourceLocation("elements/elements.png"), 18, 18, 24 , 8);
	public static GuiLocation FILLABLE_WIDE = new GuiLocation(getResourceLocation("elements/elements.png"), 62, 36, 0 , 40);
	public static GuiLocation FILLABLE_WIDE_OVERLAY = new GuiLocation(getResourceLocation("elements/elements.png"), 60, 34, 0 , 76);

}
