package squedgy.lavasources;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class CustomRegistryUtil {
	public static <T extends IForgeRegistryEntry> T getRegistryEntry(Class<T> classType, ResourceLocation rl){
		return (T) GameRegistry.findRegistry(classType).getValue(rl);
	}

	public static <T extends IForgeRegistryEntry> T getRegistryEntry(Class<T> classType, String name){
		return getRegistryEntry(classType, new ResourceLocation(name.indexOf(':') < 0 ? LavaSources.MOD_ID + ":" + name : name));
	}
}
