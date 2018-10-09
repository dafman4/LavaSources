package squedgy.lavasources.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import squedgy.lavasources.capabilities.CapacityAndStorageWrapper;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.capabilities.FluidHandler;
import squedgy.lavasources.generic.IPersistentInventory;

public class TileEntityLavaStation extends ModLockableTileEntity implements IPersistentInventory {
//<editor-fold defaultstate=collapsed desc=". . . . Fields/Constructors">
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	private EnumFacing facing;
	private FluidHandler fluids;
	private boolean destroyedByCreative;
	private static final String WATER_TANK_TAG = "water", LAVA_TANK_TAG = "lava";

	public TileEntityLavaStation(){
		super("lava_station");
		facing = EnumFacing.NORTH;
		fluids = new FluidHandler(false, 1 , 0);
		fluids.addFluidTank(new ModFluidTank(new CapacityAndStorageWrapper(1000), new FluidStack(FluidRegistry.LAVA, 0), 1000, new FluidStack[] {}));
		fluids.addFluidTank(new ModFluidTank(new CapacityAndStorageWrapper(1000), new FluidStack(FluidRegistry.WATER, 0),1000, new FluidStack[] {}));
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Lockable Methods">

	@Override
	public NonNullList<ItemStack> getInventory() { return inventory; }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemStack = inventory.get(index);
		boolean flag  = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
		if(stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
		inventory.set(index,itemStack);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) { return false; }

	@Override //To be determined
	public int getField(int id) { return 0; }

	@Override //to be determined
	public void setField(int id, int value) { }

	@Override //to be determined
	public int getFieldCount() { return 0; }

	@Override //To be determined
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) { return null; }

	@Override
	public String getGuiID() { return "lavasources:lava_station"; }

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Persistent Inventory">
	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound) {
        compound.setTag(WATER_TANK_TAG, fluids.getFluidTank(new FluidStack(FluidRegistry.WATER, 0)).serializeNBT());
        compound.setTag(LAVA_TANK_TAG, fluids.getFluidTank(new FluidStack(FluidRegistry.LAVA, 0)).serializeNBT());
	    return compound;
	}

	@Override
	public void readItem(NBTTagCompound compound){
		if(compound.hasKey(WATER_TANK_TAG)) this.fluids.getFluidTank(new FluidStack(FluidRegistry.WATER, 0)).deserializeNBT(compound.getCompoundTag(WATER_TANK_TAG));
		if(compound.hasKey(LAVA_TANK_TAG )) this.fluids.getFluidTank(new FluidStack(FluidRegistry.LAVA , 0)).deserializeNBT(compound.getCompoundTag(LAVA_TANK_TAG ));
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
