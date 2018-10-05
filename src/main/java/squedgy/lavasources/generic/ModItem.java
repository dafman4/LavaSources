package squedgy.lavasources.generic;

import net.minecraft.item.Item;
import squedgy.lavasources.LavaSources;

/**
 *
 * @author David
 */
public abstract class ModItem extends Item implements IPotentialOreDictionaryMember{
	
	public ModItem(String unlocName){
		this(unlocName, 64);
	}
	
	public ModItem(String unlocalizedName, int stackSize){
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(LavaSources.MOD_ID, unlocalizedName);
		this.setCreativeTab(LavaSources.CREATIVE_TAB);
		this.setMaxStackSize(stackSize);
		this.setMaxDamage(0);
	}

    @Override
    public boolean hasOreDictionaryName() { return false; }

    @Override
    public String getOreDictionaryName() { return null; }
	
}
