package squedgy.lavasources.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import squedgy.lavasources.enums.EnumGuiElements;
import squedgy.lavasources.gui.GuiCoreModifier;
import squedgy.lavasources.gui.GuiLiquefier;
import squedgy.lavasources.inventory.ContainerCoreModifier;
import squedgy.lavasources.inventory.ContainerLiquefier;

/**
 *
 * @author David
 */
public class ModGuis implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
		if(te != null){
			if(ID == EnumGuiElements.CORE_MODIFIER.ordinal()){
				return new ContainerCoreModifier(player.inventory, (IInventory) te);
			}else if(ID == EnumGuiElements.LIQUIFIER.ordinal()){
				return new ContainerLiquefier(player.inventory, (IInventory) te);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
		if(te != null){
			if(ID == EnumGuiElements.CORE_MODIFIER.ordinal()){
				return new GuiCoreModifier(player.inventory, (IInventory) te);
			}else if(ID == EnumGuiElements.LIQUIFIER.ordinal()){
				return new GuiLiquefier(player.inventory, (IInventory) te);
			}
		}
		return null;
	}

}
