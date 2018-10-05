package squedgy.lavasources.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import squedgy.lavasources.init.ModBlocks;

/**
 *
 * @author David
 */
public class CreativeTabLavaSources extends CreativeTabs{

	public CreativeTabLavaSources(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return ModBlocks.ITEM_BLOCKS.get(ModBlocks.BLOCKS.get(ModBlocks.LAVA_SOURCE)).getDefaultInstance();
	}

}
