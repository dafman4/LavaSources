package squedgy.lavasources.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.research.Research;

import java.util.List;

public class EventListener {

	@SubscribeEvent
	public void onLogIn(PlayerEvent.PlayerLoggedInEvent event){
		String message = "";
		EntityPlayer player = event.player;
		if(player.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)){
			IPlayerResearchCapability cap = player.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
			List<Research> research = cap.getResearch();
			if(research.size() > 0) for(Research r : research) message += r.getName() + ", ";
			else message += "no research topics";
		}else message += "no research capabilities.";

		LavaSources.writeMessage(getClass(), "player" + player.getName() + " logged in with ");
	}

}
