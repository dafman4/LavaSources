package squedgy.lavasources;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import squedgy.lavasources.events.EventListener;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModRecipes;
import squedgy.lavasources.init.ModResearch;

/**
 *
 * @author David
 */
public abstract class CommonProxy {
	
	public void preInit() throws Exception{
        ModFluids.RegistrationHandler.registerFluids();
		ModCapabilities.RegistryHandler.loadCapabilities();
		ModResearch.RegistryHandler.register();
		ModRecipes.RegistryHandler.register();
		MinecraftForge.EVENT_BUS.register(new EventListener());
	}
	
	public void init(){
		
	}
	
	public void postInit(){
		
	}
	
	// helper to determine whether the given player is in creative mode
	//  not necessary for most examples
	abstract public boolean playerIsInCreativeMode(EntityPlayer player);

	/**
	 * is this a dedicated server?
	 * @return true if this is a dedicated server, false otherwise
	 */
	abstract public boolean isDedicatedServer();
}
