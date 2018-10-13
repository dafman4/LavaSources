package squedgy.lavasources.item;

import net.minecraft.item.Item;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.blocksoritems.IModCraftable;
import squedgy.lavasources.generic.blocksoritems.IPotentialOreDictionaryMember;
import squedgy.lavasources.research.Research;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public abstract class ModItem extends Item implements IPotentialOreDictionaryMember, IModCraftable {

	private final List<Research> required = new ArrayList<>();

	public ModItem(String unlocName){ this(unlocName, 64); }
	
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

	@Override
	public final List<Research> getRequirements() {
		return new ArrayList<>(required);
	}

	@Override
	public final void addRequirement(Research requirement) {
		if(this.required.indexOf(requirement) < 0) required.add(requirement);
	}
}
