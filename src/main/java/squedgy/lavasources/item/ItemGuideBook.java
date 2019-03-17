package squedgy.lavasources.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.enums.EnumGuiElements;

public class ItemGuideBook extends ModItem {
//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	public ItemGuideBook(String unlocName) { super(unlocName, 1); }

	public ItemGuideBook(){ this("guide_book"); }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Item">

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if(worldIn.isRemote) playerIn.openGui(LavaSources.INSTANCE, EnumGuiElements.GUIDE_BOOK.ordinal(), worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		return new ActionResult<ItemStack>( EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}


//</editor-fold>
}
