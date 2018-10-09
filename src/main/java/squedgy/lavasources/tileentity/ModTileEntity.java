package squedgy.lavasources.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.inventory.ContainerLiquefier;
import squedgy.lavasources.tileentity.TileEntityLiquefier;

public abstract class ModTileEntity extends TileEntity {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	protected final String CLASS_TAG = "class";

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity Overrides">

	@Override
	public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt ){
		readFromNBT(pkt.getNbtCompound());
		notifyBlockUpdate();
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket(){ return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag()); }

	@Override
	public final NBTTagCompound getUpdateTag(){ return writeToNBT(super.getUpdateTag()); }

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag(CLASS_TAG, writeItem(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);
		if(compound.hasKey(CLASS_TAG))readItem(compound.getCompoundTag(CLASS_TAG));
	}

	protected void notifyBlockUpdate(){
		LavaSources.writeMessage(getClass(), "notifyBlockUpdate()");
		if(this instanceof TileEntityLiquefier) LavaSources.writeMessage(getClass(), "" + ((TileEntityLiquefier)this).isLiquefying());
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		world.notifyBlockUpdate(pos, state, state, 1 & 2);
	}

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Abstract/Helpers">

	public abstract NBTTagCompound writeItem(NBTTagCompound compound);
	public abstract void readItem(NBTTagCompound compound);

//</editor-fold>

}
