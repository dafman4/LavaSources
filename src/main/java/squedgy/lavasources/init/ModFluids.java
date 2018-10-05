package squedgy.lavasources.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import squedgy.lavasources.LavaSources;

/**
 *
 * @author David
 */
public class ModFluids {

	public static final Fluid LIQUID_REDSTONE = new Fluid("liquid_redstone", new ResourceLocation(LavaSources.MOD_ID, "blocks/fluids/liquid_redstone"), new ResourceLocation(LavaSources.MOD_ID, "blocks/fluids/liquid_redstone"));
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler{
        public static void registerFluids(){
            FluidRegistry.registerFluid(LIQUID_REDSTONE);
            FluidRegistry.addBucketForFluid(LIQUID_REDSTONE);
        }
	}
	
}
