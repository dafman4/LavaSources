package squedgy.lavasources;

import net.minecraft.entity.player.EntityPlayer;
import squedgy.lavasources.init.ModFluids;

/**
 *
 * @author David
 */
public abstract class CommonProxy {
	
	public void preInit() throws Exception{
        ModFluids.RegistrationHandler.registerFluids();
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
