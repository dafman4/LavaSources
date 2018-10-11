package squedgy.lavasources.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.registries.RegistryBuilder;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.gui.elements.ResearchTab;
import squedgy.lavasources.helper.GuiLocation;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.init.ModRegistries;
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
			if(research.size() > 0) for(Research r : research) if(r != null) message += r.getName() + ", ";
			else message += "no research topics";
		}else message += "no research capabilities.";

		LavaSources.writeMessage(getClass(), "\n\n\nplayer" + player.getName() + " logged in with " + message + "\n\n\n");
	}

	@SubscribeEvent
	public void registryRegister(RegistryEvent.NewRegistry event){
		LavaSources.writeMessage(getClass(), "\n\n\n\tregistering registries.");
		ModRegistries.RESEARCH_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "b_research_registry")).setType(Research.class).disableSaving().create();
		ModRegistries.GUI_LOCATION_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "a_gui_location_registry")).setType(GuiLocation.class).disableSaving().create();
		ModRegistries.RESEARCH_TAB_REGISTRY = new RegistryBuilder().setName(new ResourceLocation(LavaSources.MOD_ID, "c_research_tab_registry")).setType(ResearchTab.class).disableSaving().create();
	}

}
