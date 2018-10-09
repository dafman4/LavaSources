package squedgy.lavasources.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import squedgy.lavasources.enums.EnumGuiElements;
import squedgy.lavasources.tileentity.ModLockableTileEntity;
import squedgy.lavasources.tileentity.ModTileEntity;
import squedgy.lavasources.tileentity.TileEntityLiquefier;

/**
 *
 * @author David
 */
public class ModGuis implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumGuiElements element = EnumGuiElements.values()[ID];
		return EnumGuiElements.values()[ID].getContainer(player.inventory, getInventory(element, player, world, x, y, z));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumGuiElements element = EnumGuiElements.values()[ID];
		return EnumGuiElements.values()[ID].getGui(player.inventory, getInventory(element, player, world,  x, y, z));
	}

	private IInventory getInventory(EnumGuiElements element, EntityPlayer player, World world, int x, int y, int z){
		IInventory ret = null;
		if(!element.ITEM){
			TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
			if(te instanceof ModLockableTileEntity) ret = (IInventory) te;
		}
		return ret;
	}

}
