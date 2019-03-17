package squedgy.lavasources.init;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.gui.elements.ElementTextDisplay;
import squedgy.lavasources.gui.elements.GuiElement;
import squedgy.lavasources.research.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.research.Research;
import squedgy.lavasources.research.ResearchButton;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.util.*;

@ObjectHolder(LavaSources.MOD_ID)
public class ModResearch {

	public static final ResearchTab DEFAULT_TAB = null;
	public static final Research TEST = null;

	@Mod.EventBusSubscriber
	public static class RegistryHandler{

		public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		public static void register(){  }

	}
}
