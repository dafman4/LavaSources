package squedgy.lavasources;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 *
 * @author David
 */
public class ServerProxy extends CommonProxy{
	
	
	@Override
	public void preInit() throws Exception{;
		super.preInit();
	}
	
	@Override
	public void init(){
		super.init();
	}
	
	@Override
	public void postInit(){
		super.postInit();
	}
	
	
	@Override
	public boolean playerIsInCreativeMode(EntityPlayer player) {
		if(player instanceof EntityPlayerMP) return ((EntityPlayerMP)player).interactionManager.isCreative();
		return false;
	}

	@Override
	public boolean isDedicatedServer() {return true;}

}
