package squedgy.lavasources;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import squedgy.lavasources.helper.StringUtils;

public abstract class CustomRegistryUtil {
	public static <T extends IForgeRegistryEntry> T getRegistryEntry(Class<T> classType, ResourceLocation rl){
		return (T) GameRegistry.findRegistry(classType).getValue(rl);
	}

	public static <T extends IForgeRegistryEntry> T getRegistryEntry(Class<T> classType, String name){
		return getRegistryEntry(classType, StringUtils.getResourceLocation(LavaSources.MOD_ID, name));
	}
}
