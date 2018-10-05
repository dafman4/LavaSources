package squedgy.lavasources.init;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
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

            ModelLoader.registerItemVariants(Item.getItemFromBlock(ModBlocks.LIQUID_REDSTONE));
            ModelResourceLocation location = new ModelResourceLocation(LavaSources.MOD_ID + ":fluid", "liquid_redstone");
            ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(LIQUID_REDSTONE.getBlock()), stack -> location);

            ModelLoader.setCustomStateMapper(LIQUID_REDSTONE.getBlock(), new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return location;
                }
            });

        }
	}
	
}
