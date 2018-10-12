package squedgy.lavasources.research;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.ModGui;
import squedgy.lavasources.gui.elements.GuiButton;
import squedgy.lavasources.helper.GuiLocation;

@SuppressWarnings("ConstantConditions")
public class ResearchButton extends GuiButton {
	private static int id = 0;
	@ObjectHolder("lavasources:link_image_border")
	public static final GuiLocation link_image_border = GuiLocation.getGuiLocation("link_image_border");
	private Research research;
	private String description;
	private GuiLocation drawImage;
	private final int saveX, saveY;
	private ModGui drawer;


	public ResearchButton(int x, int y, Research research, String description, GuiLocation drawImage){
		super(++id, x*(link_image_border.width+2), y*(link_image_border.height+2),"", link_image_border, link_image_border, link_image_border, link_image_border, link_image_border);
		this.description = description;
		this.drawImage = drawImage;
		this.research = research;
		saveX = x;
		saveY = y;
		LavaSources.writeMessage(getClass(), "instantiated with " + toString());
		LavaSources.writeMessage(getClass(), "drawImage = " + this.drawImage);
	}

	@Override
	protected void drawButtonBackground(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		int drawWidth = link_image_border.width, drawHeight = link_image_border.height;

		drawTexturedModalRect(x + 1, y + 1, link_image_border, drawer);

		drawTexturedModalRect(x + 2, y + 2, drawImage, drawer);

		if(mouseX > x && mouseX < x + width && mouseY > y && mouseY < y+height) GuiUtils.drawHoveringText(java.util.Arrays.asList(description), mouseX, mouseY, drawer.width, drawer.height, width*8, mc.fontRenderer);
	}

	public static int getId() { return id; }

	public Research getResearch() { return research; }

	public String getDescription() { return description; }

	public GuiLocation getDrawImage() { return drawImage; }

	public int getSaveX() { return saveX; }

	public int getSaveY() { return saveY; }

	public void setDrawer(ModGui drawer){ this.drawer = drawer; }

	private static void drawTexturedModalRect(int x, int y, GuiLocation image, ModGui drawer){
		drawer.mc.renderEngine.bindTexture(image.location);
		drawTexturedModalRect(x, y, image.textureX, image.textureY, image.height, image.width, drawer);
	}

	private static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int height, int width, ModGui drawer){
		drawer.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}

	@Override
	public String toString() {
		return "ResearchButton{" +
				"research=" + research +
				", description='" + description + '\'' +
				", drawImage=" + drawImage +
				", saveX=" + saveX +
				", saveY=" + saveY +
				'}';
	}
}
