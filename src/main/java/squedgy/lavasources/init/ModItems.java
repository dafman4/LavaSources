package squedgy.lavasources.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.item.ModItem;
import squedgy.lavasources.item.*;

/**
 *
 * @author David
 */
@ObjectHolder(LavaSources.MOD_ID)
public class ModItems {
	public static final ItemUpgradeCard UPGRADE_CARD = new ItemUpgradeCard();
	public static final ItemCorePiece CORE_PIECE = new ItemCorePiece();
	public static final ItemLavaCore LAVA_CORE = new ItemLavaCore();
	public static final ItemEmptyCore EMPTY_CORE = new ItemEmptyCore();
	public static final ItemEnergyCore ENERGY_CORE = new ItemEnergyCore();
	public static final ItemGuideBook GUIDE_BOOK = new ItemGuideBook();
	
	@Mod.EventBusSubscriber
	public static class RegistryHelper{

        private static final ModItem[] ITEMS = {
            UPGRADE_CARD,
            CORE_PIECE,
            LAVA_CORE,
            EMPTY_CORE,
            ENERGY_CORE,
	        GUIDE_BOOK
        };
		
		@SubscribeEvent//Register OreDictionary here
		public static void registerItems(RegistryEvent.Register<Item> event){
			event.getRegistry().registerAll(ITEMS);
			for(ModItem i : ITEMS) if(i.hasOreDictionaryName()) OreDictionary.registerOre(i.getOreDictionaryName(), i);
		}
		
		@SubscribeEvent
		@SideOnly(CLIENT)
		public static void registerModels(ModelRegistryEvent event){
			for(Item t : ITEMS){
				ModelLoader.setCustomModelResourceLocation(t, 0, new ModelResourceLocation(t.getRegistryName(), "inventory"));
			}
		}
		
	}
}
