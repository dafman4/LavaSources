package squedgy.lavasources.generic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraftforge.common.util.Constants;

public interface IPersistentInventory {
	public abstract NBTTagCompound writeItem(NBTTagCompound compound);
	public abstract boolean shouldDropSpecial();
	public abstract boolean isDestroyedByCreative();
	public abstract void setDestroyedByCreative(boolean destroyedByCreative);
}
