package squedgy.lavasources.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import squedgy.lavasources.capabilities.FluidTankWrapper;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.capabilities.FluidHandler;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;

public class TileEntityLavaStation extends TileEntityLockable implements IPersistentInventory {
//<editor-fold defaultstate=collapsed desc=". . . . Fields">
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private EnumFacing facing;
	private FluidHandler fluids;
	private boolean destroyedByCreative;
	private static final String WATER_TANK_TAG = "water", LAVA_TANK_TAG = "lava";
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	public TileEntityLavaStation(){
		facing = EnumFacing.NORTH;
		fluids = new FluidHandler(false, 1 , 0);
		fluids.addFluidTank(new ModFluidTank(new FluidTankWrapper(1000), new FluidStack(FluidRegistry.LAVA, 0), 1000, new FluidStack[] {}));
		fluids.addFluidTank(new ModFluidTank(new FluidTankWrapper(1000), new FluidStack(FluidRegistry.WATER, 0),1000, new FluidStack[] {}));
	}
//</editor-fold>

//<editor-fold defaultstate=collapsed desc=". . . . Lockable Methods">
	@Override
	public int getSizeInventory() { return inventory.size(); }

	@Override
	public boolean isEmpty() {
		boolean ret = false;

		for(ItemStack stack : this.inventory){
			ret = ret | stack.isEmpty();
			if(ret) break;
		}

		return ret;
	}

	@Override
	public ItemStack getStackInSlot(int index) { return this.inventory.get(index); }

	@Override
	public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(inventory, index, count); }

	@Override
	public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(inventory, index); }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemStack = inventory.get(index);
		boolean flag  = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
		if(stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
		inventory.set(index,itemStack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world.getTileEntity(this.pos) != this) { return false; }
		else { return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D; }
	}

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) { return false; }

	@Override //To be determined
	public int getField(int id) { return 0; }

	@Override //to be determined
	public void setField(int id, int value) { }

	@Override //to be determined
	public int getFieldCount() { return 0; }

	@Override
	public void clear() { this.inventory.clear(); }

	@Override //To be determined
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) { return null; }

	@Override
	public String getGuiID() { return "lavasources:lava_station"; }

	@Override
	public String getName() { return "container.lava_station"; }

	@Override
	public boolean hasCustomName() { return false; }

//</editor-fold>

//<editor-fold defaultstate=collapsed desc=". . . . Persistent Inventory">
	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound) {
        compound.setTag(WATER_TANK_TAG, fluids.getFluidTank(new FluidStack(FluidRegistry.WATER, 0)).serializeNBT());
        compound.setTag(LAVA_TANK_TAG, fluids.getFluidTank(new FluidStack(FluidRegistry.LAVA, 0)).serializeNBT());
	    return compound;
	}

	@Override
	public boolean shouldDropSpecial() { return true; }

	@Override
	public boolean isDestroyedByCreative() { return destroyedByCreative; }

	@Override
	public void setDestroyedByCreative(boolean destroyedByCreative) { this.destroyedByCreative = destroyedByCreative;}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">

    public void setFacing(EnumFacing opposite) { this.facing = opposite; }

    public EnumFacing getFacing(){ return facing; }

    public FluidHandler getFluids() { return fluids; }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity Override">

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt ){
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket(){ return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound())); }

    @Override
    public NBTTagCompound getUpdateTag(){ return writeToNBT(super.getUpdateTag()); }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) { return writeItem(super.writeToNBT(compound)); }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
	    super.readFromNBT(compound);
	    if(compound.hasKey(WATER_TANK_TAG)) this.fluids.getFluidTank(new FluidStack(FluidRegistry.WATER, 0)).deserializeNBT(compound.getCompoundTag(WATER_TANK_TAG));
        if(compound.hasKey(LAVA_TANK_TAG )) this.fluids.getFluidTank(new FluidStack(FluidRegistry.LAVA , 0)).deserializeNBT(compound.getCompoundTag(LAVA_TANK_TAG ));
	}

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) fluids;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if( capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }
//</editor-fold>

}
