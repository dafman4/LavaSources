package squedgy.lavasources.helper;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.ModContainer;

public class StringUtils {

	public static ResourceLocation getResourceLocation(ModContainer mod, String location){
		return getResourceLocation(mod.getModId(), location);
	}

	public static ResourceLocation getResourceLocation(JsonContext mod, String location){
		return getResourceLocation(mod.getModId(), location);
	}

	public static ResourceLocation getResourceLocation(String modId, String location){
		if(location.indexOf(':') < 0) return new ResourceLocation(modId, location);
		return new ResourceLocation(location);
	}

}
