package squedgy.lavasources.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.capabilities.PlayerResearchCapability;

public class ModCapabilities {

	@CapabilityInject(IPlayerResearchCapability.class)
	public static final Capability<IPlayerResearchCapability> PLAYER_RESEARCH_CAPABILITY = null;

	@Mod.EventBusSubscriber(modid = LavaSources.MOD_ID)
	public static class RegistryHandler{

		@SubscribeEvent
		public static void registerCapabilities(AttachCapabilitiesEvent<Entity> event){
			if(event.getObject() instanceof EntityPlayer) event.addCapability(new ResourceLocation(LavaSources.MOD_ID, "player_research"), new PlayerResearchCapability.Provider());
		}

		public static void loadCapabilities(){
			CapabilityManager.INSTANCE.register(IPlayerResearchCapability.class,new IPlayerResearchCapability.Storage(), PlayerResearchCapability::new);
		}
	}

}
