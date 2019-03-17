package squedgy.lavasources;

import net.minecraft.entity.player.EntityPlayer;
import squedgy.lavasources.init.*;
import squedgy.lavasources.research.Research;

/**
 *
 * @author David
 */
public abstract class CommonProxy {
	
	public void preInit() throws Exception{
		ModRegistries.RegistryHandler.preInit();
        ModFluids.RegistrationHandler.registerFluids();
		ModResearch.RegistryHandler.register();
		ModCapabilities.RegistryHandler.loadCapabilities();
	}
	
	public void init(){
		Research.initAllResearch();
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
