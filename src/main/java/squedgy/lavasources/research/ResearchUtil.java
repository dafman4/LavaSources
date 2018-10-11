package squedgy.lavasources.research;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squedgy.lavasources.LavaSources;

public class ResearchUtil {

	public static Research getResearch(String name){
		ResourceLocation rl = new ResourceLocation(name.indexOf(':') < 0 ? LavaSources.MOD_ID + ":" + name : name);
		return GameRegistry.findRegistry(Research.class).getValue(rl);
	}

}
