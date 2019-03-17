package squedgy.lavasources.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;


public abstract class ModLockableTileEntity extends ModTileEntity implements ILockableContainer {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">

	protected final String CODE_TAG = "LockCode", NAME_TAG ="Name", CUSTOM_NAME_TAG = "CustomName", INVENTORY_TAG = "inventory";
	private LockCode code;
	private String name;
	private boolean customName;
	private IItemHandler handler;

	private ModLockableTileEntity(LockCode code, String name){
		this.code = code;
		this.name = "container." + name;
	}

	public ModLockableTileEntity(String unlocalizedName){ this(LockCode.EMPTY_CODE, unlocalizedName); }

//</editor-fold>

//<editor-fold defaultstate=collapsed" desc=". . . . Getters/Setters">

	public void setName(String name) { this.name = name; this.customName = true; }

	public abstract NonNullList<ItemStack> getInventory();

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . LockableContainer Overrides">

	@Override
	public final boolean hasCustomName(){ return customName; }

	@Override
	public final String getName(){ return name; }

	@Override
	public final boolean isLocked() { return this.code != null && !this.code.isEmpty(); }

	@Override
	public final LockCode getLockCode() { return this.code; }

	@Override
	public final void setLockCode(LockCode code) { this.code = code; }

	@Override
	public final ITextComponent getDisplayName(){
		return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
	}

	@Override
	public final void openInventory(EntityPlayer player) { }

	@Override
	public final void closeInventory(EntityPlayer player) { }

	@Override
	public final int getInventoryStackLimit() { return 64; }

	@Override
	public final boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world.getTileEntity(this.pos) != this) { return false; }
		else { return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D; }
	}

	@Override
	public final int getSizeInventory() { return getInventory().size(); }

	@Override
	public final boolean isEmpty(){ return getInventory().stream().allMatch(ItemStack::isEmpty); }

	@Override
	public final ItemStack getStackInSlot(int index) { return index >= 0 && index < getSizeInventory() ? getInventory().get(index) : ItemStack.EMPTY; }

	@Override
	public final ItemStack decrStackSize(int index, int count) {
		ItemStack ret = ItemStackHelper.getAndSplit(getInventory(), index, count);
		if(!ret.isEmpty()) markDirty();
		return ret;
	}

	@Override
	public final ItemStack removeStackFromSlot(int index) {
		ItemStack ret = ItemStackHelper.getAndRemove(getInventory(), index);
		if(!ret.isEmpty())markDirty();
		return ItemStackHelper.getAndRemove(getInventory(), index);
	}

	@Override
	public final void clear(){ getInventory().clear(); }

//</editor-fold>

	//if it has an inventory we're saving the shit outta it :3
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);
		NBTTagCompound classTag = compound.getCompoundTag(CLASS_TAG);
		NBTTagCompound codeTag = new NBTTagCompound();
		code.toNBT(codeTag);
		classTag.setTag(CODE_TAG, codeTag);
		classTag.setString(NAME_TAG, name);
		classTag.setBoolean(CUSTOM_NAME_TAG, customName);
		classTag.setTag(INVENTORY_TAG, ItemStackHelper.saveAllItems(new NBTTagCompound(), getInventory()));
		return compound;
	}
	//and reading it too
	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);
		if(compound.hasKey(CLASS_TAG)){
			NBTTagCompound classTag = compound.getCompoundTag(CLASS_TAG);
			if(classTag.hasKey(CODE_TAG)) code = LockCode.fromNBT(classTag.getCompoundTag(CODE_TAG));
			if(classTag.hasKey(NAME_TAG)) name = classTag.getString(NAME_TAG);
			if(classTag.hasKey(CUSTOM_NAME_TAG)) customName = classTag.getBoolean(CUSTOM_NAME_TAG);
			if(classTag.hasKey(INVENTORY_TAG)) ItemStackHelper.loadAllItems(classTag.getCompoundTag(INVENTORY_TAG), getInventory());
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(handler == null) handler = new InvWrapper(this);
			return (T)  handler;
		}
		return super.getCapability(capability, facing);
	}

}
