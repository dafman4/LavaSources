package squedgy.lavasources.tileentity;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.block.BlockLiquefier;
import squedgy.lavasources.capabilities.FluidHandler;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.enums.EnumEnergyTier;
import squedgy.lavasources.enums.EnumFluidTier;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.ContainerLiquefier;

/**
 *
 * @author David
 */
public class TileEntityLiquefier extends TileEntityLockable implements IUpgradeable, ITickable, ISidedInventory, IPersistentInventory {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields">
	public enum SlotEnum{ INPUT_SLOT }
	public static final List<Fluid> POSSIBLE_FLUIDS = Arrays.asList(ModFluids.LIQUID_REDSTONE);
	private static final int[] SLOTS = {SlotEnum.INPUT_SLOT.ordinal()};
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	public static final int FLUID_PER_REDSTONE = 100;
	private int energyPerTick;
	private EnumUpgradeTier tier;
	private ModEnergyStorage energy;
	private ModFluidTank fluids;
	private EnumFacing facing;
	private boolean liquifying, destroyedByCreative;

//</editor-fold>	

//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	public TileEntityLiquefier(EnumUpgradeTier tier){
		this.tier = tier;
		this.updateTierRelatedFields(0 ,0);
	}
	
	public TileEntityLiquefier(){
		this(EnumUpgradeTier.BASIC);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public EnumUpgradeTier getTier() {
		return tier;
	}

	public ModFluidTank getFluids() {
		return fluids;
	}
	
	public boolean getLiquifying(){
		return this.liquifying;
	}

	public void setLiquifying(boolean newValue){
		this.liquifying = newValue;
	}
	
	public void setFacing(EnumFacing facing){
		boolean test = false;
		for(EnumFacing face : EnumFacing.HORIZONTALS)
			test = test | facing == face;
		if(test)
			this.facing = facing;
	}
	
	public EnumFacing getFacing(){
		return facing;
	}
	
	public ModEnergyStorage getEnergy() {
		return energy;
	}
	
	public List<ItemStack> getInventory(){
		return this.inventory;
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	
	@Override
	public void update() {
		boolean testLiquifying = this.liquifying;
		
		if(!world.isRemote){
			if((this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).isItemEqual(Items.REDSTONE.getDefaultInstance()) || this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).isItemEqual(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK).getDefaultInstance()))
                && this.energy.getEnergyStored() >= energyPerTick){
			    boolean isBlock = !this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).isItemEqual(Items.REDSTONE.getDefaultInstance());
				if(FLUID_PER_REDSTONE * (isBlock ? 10 : 1) == fluids.internalFill(new FluidStack(ModFluids.LIQUID_REDSTONE, FLUID_PER_REDSTONE * (isBlock ? 10 : 1)), false)){
					if(energy.internalExtract(energyPerTick * (isBlock ? 12 : 1), true) == energyPerTick * (isBlock ? 12 : 1)){
						fluids.internalFill(new FluidStack(ModFluids.LIQUID_REDSTONE, FLUID_PER_REDSTONE * (isBlock ? 10 : 1)), true);
						energy.internalExtract(energyPerTick * (isBlock ? 12 : 1), false);
						this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).shrink(1);
						this.liquifying = true;
					}
				}
			}else if(world.getBlockState(pos).getValue(BlockLiquefier.LIQUEFYING))
				this.liquifying = false;
			if(fluids.getFluidAmount() > 0){
				for(EnumFacing f : EnumFacing.values()){
					if(fluids.getFluidAmount() == 0) break;
					TileEntity te = world.getTileEntity(pos.offset(f));
					if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())){
						IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
						int extracted = fluids.drain(fluids.getMaxDrain(), false).amount,
							received = handler.fill(new FluidStack(ModFluids.LIQUID_REDSTONE, extracted), false);
						received = handler.fill(new FluidStack(ModFluids.LIQUID_REDSTONE, Math.min(extracted,received)), true);
						if(received > 0){
							fluids.drain(extracted, true);
						}
					}
				}
			}
		}
		
		if(this.liquifying != testLiquifying){
			markDirty();
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity Override">
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt ){ readFromNBT(pkt.getNbtCompound()); }
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){ return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound())); }
	
	@Override
	public NBTTagCompound getUpdateTag(){ return writeToNBT(super.getUpdateTag()); }

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) { return writeItem(super.writeToNBT(compound)); }
	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound) {
		compound.setInteger("tier", tier.LEVEL);
		compound.setInteger("energy_stored", energy.getPowerStored());
		compound.setInteger("fluid_amount", fluids.getFluidAmount());
		ItemStackHelper.saveAllItems(compound, this.inventory);
		return compound;
	}

	@Override
	public boolean shouldDropSpecial() { return fluids.getFluidAmount() > 0 || energy.getEnergyStored() > 0 || this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()) != ItemStack.EMPTY || this.tier.LEVEL > 0; }

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		this.updateTierRelatedFields(compound.getInteger("energy_stored"), compound.getInteger("fluid_amount"));
		ItemStackHelper.loadAllItems(compound, inventory);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) fluids;
		return super.getCapability(capability, facing); 
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		if( capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable Overrides">
	@Override
	public boolean upgrade(EnumUpgradeTier tier) {
		boolean ret = false;
		if(EnumUpgradeTier.isUpgradeFor(this, tier)){
			this.tier = this.tier.getUpgrade();
			this.updateTierRelatedFields();
			ret = true;
			markDirty();
		}
		return ret;
	}

	@Override
	public EnumUpgradeTier getCurrentTier() { return this.tier; }
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Lockable Overrides">

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
	public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(this.inventory, index, count); }

	@Override
	public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(this.inventory, index); }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.inventory.set(index, stack);
		if(stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public int getInventoryStackLimit() { return 64; }

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        else return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) { }

	@Override
	public void closeInventory(EntityPlayer player) { }

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) { return stack.getItem() == Items.REDSTONE; }

	@Override
	public int getField(int id) {
		switch(id){
			case 0: return this.fluids.getFluid().amount;
			case 1: return this.energy.getEnergyStored();
			case 2: return this.fluids.getCapacity();
			case 3: return this.energy.getMaxEnergyStored();
			case 4: return this.getLiquifying() ? 1 : 0;
			default:return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id){
			case 0:
				this.fluids.setFluidAmount(value);
				break;
			case 1:
				this.energy.setPowerStored(value);
				break;
			case 2:
				this.fluids.setCapacity(value);
				break;
			case 3:
				this.energy.setMaxPowerStored(value);
				break;
			case 4:
				this.setLiquifying(value == 1);
				break;
		}
	}

	@Override
	public int getFieldCount(){ return 5; }

	@Override
	public void clear() { this.inventory.clear(); }

	@Override
	public String getName() { return "container.liquefier"; }

	@Override
	public boolean hasCustomName() { return false; }

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) { return new ContainerLiquefier(playerInventory, this); }

	@Override
	public String getGuiID() { return "lavasources:liquefier"; }
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Sided Inventory Overrides">
	@Override
	public int[] getSlotsForFace(EnumFacing side) { return SLOTS; }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if(index == SlotEnum.INPUT_SLOT.ordinal()) return itemStackIn.getItem() == ModItems.EMPTY_CORE;
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return stack.getItem() != ModItems.EMPTY_CORE; }
	
//</editor-fold>

	@Override
	public boolean isDestroyedByCreative() { return destroyedByCreative; }

	@Override
	public void setDestroyedByCreative(boolean destroyedByCreative) { this.destroyedByCreative = destroyedByCreative; }

	private void updateTierRelatedFields(){ updateTierRelatedFields(energy.getEnergyStored(), fluids.getFluidAmount()); }

	private void updateTierRelatedFields(int energyStored, int fluidHeld){
		fluids = tier.getFluidTier().getFluidTank(new FluidStack(ModFluids.LIQUID_REDSTONE, fluidHeld), false);
		energy = tier.getEnergyTier().getEnergyStorage(energyStored);
		this.energyPerTick = tier.getEnergyTier().getRequired();
	}
}
