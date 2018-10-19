package squedgy.lavasources.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import com.google.common.base.Preconditions;

import java.util.*;

import net.minecraftforge.client.event.ModelRegistryEvent;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import net.minecraftforge.fml.relauncher.SideOnly;
import squedgy.lavasources.block.*;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.block.fluid.BlockLiquidRedstone;
import squedgy.lavasources.block.ModBlock;

/**
 *
 * @author David
 */
public class ModBlocks {
	public static final int ENERGY_GENERATOR = 0;
	public static final int LAVA_SOURCE= 1;
	public static final int ENERGY_CABLE= 2;
	public static final int CORE_MODIFIER= 3;
	public static final int LIQUEFIER = 4;
	public static final int LAVA_STATION = 5;
	public static final BlockLiquidRedstone LIQUID_REDSTONE = new BlockLiquidRedstone();

	public static final List<ModBlock> BLOCKS = Arrays.asList(new BlockEnergyGenerator(),
			(BlockLavaSource)new BlockLavaSource().setHardness(4f).setResistance(40f),
			new BlockEnergyCable(),
			new BlockCoreModifier(),
			new BlockLiquefier(),
			new BlockLavaStation()
	);
	public static final Map<Block,ItemBlock> ITEM_BLOCKS = new HashMap<>();

	
	@Mod.EventBusSubscriber(modid = LavaSources.MOD_ID)
	public static class RegistryHandler{
		
		private static final ItemBlock[] ITEMS = {
			new ItemBlock(BLOCKS.get(ENERGY_GENERATOR)),
			new ItemBlock(BLOCKS.get(LAVA_SOURCE)),
			new ItemBlock(BLOCKS.get(ENERGY_CABLE)),
			new ItemBlock(BLOCKS.get(CORE_MODIFIER)),
			new ItemBlock(BLOCKS.get(LIQUEFIER)),
			new ItemBlock(BLOCKS.get(LAVA_STATION))
		};
		
		@SubscribeEvent
		@SideOnly(CLIENT)
		public static void registerItemBlockModels(ModelRegistryEvent event){
			for(ItemBlock b : ITEMS){
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b.getBlock()), 0, new ModelResourceLocation(Objects.requireNonNull(b.getRegistryName()), "inventory"));
			}
		}

		@SubscribeEvent
		public static void attachToBlocks(AttachCapabilitiesEvent<Block> event){ }
		
		@SubscribeEvent
		public  static void registerBlocks(RegistryEvent.Register<Block> event){;
			for(ModBlock b : BLOCKS){;
				event.getRegistry().register(b);
				if(b.hasOreDictionaryName()) OreDictionary.registerOre(b.getOreDictionaryName(), b);
			}
			event.getRegistry().register(LIQUID_REDSTONE);
		}

		@SubscribeEvent
		public static void registerItemBlocks(RegistryEvent.Register<Item> event){
			
			final IForgeRegistry<Item> registry = event.getRegistry();
			;
			for(ItemBlock b : ITEMS){
				final Block block = b.getBlock();
				final ResourceLocation location = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
				registry.register(b.setRegistryName(location));;
				ITEM_BLOCKS.put(block, b);
				
			}
			
			registerTileEntities();
		}
		
		public static void registerTileEntities(){
			for(ModBlock b : BLOCKS) if(b.hasTileEntity())registerTileEntity(b.getTileEntityClass(), b.getUnlocalizedName().substring("tile.".length()));
		}
		
		private static void registerTileEntity(final Class<? extends TileEntity> clazz, final String name){;
			GameRegistry.registerTileEntity(clazz, LavaSources.MOD_ID + ":" + name);
		}
		
	}
}
