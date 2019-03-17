package squedgy.lavasources;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import squedgy.lavasources.creativetabs.CreativeTabLavaSources;
import squedgy.lavasources.events.EventListener;
import squedgy.lavasources.helper.StringUtils;
import squedgy.lavasources.init.ModGuis;

import java.util.Arrays;


@Mod(modid=LavaSources.MOD_ID, name=LavaSources.NAME, version=LavaSources.VERSION)
public class LavaSources{
	public final static String MOD_ID = "lavasources";
	public final static String NAME = "Lava Sources";
	public final static String VERSION = "1.0";
	public final static CreativeTabs CREATIVE_TAB = new CreativeTabLavaSources("Lava Sources");
	@Instance(MOD_ID) public static LavaSources INSTANCE = null;
	
	private static Logger logger;
	static {
		MinecraftForge.EVENT_BUS.register(new EventListener());
		FluidRegistry.enableUniversalBucket();
	}
	@SidedProxy(clientSide="squedgy.lavasources.ClientProxy", serverSide="squedgy.lavasources.ServerProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws Exception{
		logger = event.getModLog();
		writeMessage(LavaSources.class, "\nStarting pre-initialization for " + NAME + " v." + VERSION);
 		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		writeMessage(LavaSources.class, "\nStarting initialization for " + NAME + " v." + VERSION);
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new ModGuis());
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		writeMessage(LavaSources.class,"\nPost init for " + NAME + " v." +VERSION);
		proxy.postInit();
	}
	
	public static String getResourceName(String resourceName){ return getResourceLocation(resourceName).toString(); }

	public static ResourceLocation getResourceLocation(String resourceName){ return StringUtils.getResourceLocation(MOD_ID, resourceName); }
	
	public static void writeMessage( Class clazz, String message){
		if(logger != null) {
//			logger.info("message: " + message);
//			logger.info("message-split: " + Arrays.asList(message.split("((\\r)?\\n)|\\r")));
			for(String s : message.split("((\\r)?\\n)|\\r")){//split new lines
				if(!s.isEmpty()) logger.info("[" + clazz.getSimpleName() + "]: " + s);
			}
		}
	}
}