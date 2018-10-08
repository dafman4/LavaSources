package squedgy.lavasources.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.generic.ModLockableTileEntity;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.ContainerLiquefier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static squedgy.lavasources.tileentity.TileEntityLiquefier.SlotEnum.INPUT_SLOT;

/**
 *
 * @author David
 */
public class TileEntityLiquefier extends ModLockableTileEntity implements IUpgradeable, ITickable, ISidedInventory, IPersistentInventory {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	public static final String TIER_TAG = "tier", ENERGY_TAG = "energy", FLUID_TAG = "fluid", LIQUEFYING_TAG = "liquefying", INVENTORY_TAG = "inventory";
	public enum SlotEnum{ INPUT_SLOT }
	public static final List<Fluid> POSSIBLE_FLUIDS = Arrays.asList(ModFluids.LIQUID_REDSTONE);
	private static final int[] SLOTS = {INPUT_SLOT.ordinal()};
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	public static final int FLUID_PER_REDSTONE = 100;
	private int energyPerTick;
	private EnumUpgradeTier tier;
	private ModEnergyStorage energy;
	private ModFluidTank fluids;
	private EnumFacing facing;
	private boolean liquefying, destroyedByCreative;

	public TileEntityLiquefier(EnumUpgradeTier tier){
		super("liquefier");
		this.tier = tier;
		this.updateTierRelatedComponents(0 ,0);
	}
	
	public TileEntityLiquefier(){
		this(EnumUpgradeTier.BASIC);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public EnumUpgradeTier getTier() { return tier; }

	public ModFluidTank getFluids() { return fluids; }
	
	public boolean isLiquefying(){ return this.liquefying; }

	public void setLiquefying(boolean newValue){
		if(newValue != liquefying) notifyBlockUpdate();
		this.liquefying = newValue;
	}
	
	public void setFacing(EnumFacing facing){ if(facing.getHorizontalIndex() != -1) this.facing = facing; }
	
	public EnumFacing getFacing(){ return facing; }
	
	public ModEnergyStorage getEnergy() { return energy; }
	
	public NonNullList<ItemStack> getInventory(){ return this.inventory; }
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	
	@Override
	public void update() {
		boolean flag = false;
		ItemStack inputSlot = inventory.get(INPUT_SLOT.ordinal());
		boolean isBlock = !inputSlot.isItemEqual(Items.REDSTONE.getDefaultInstance());
		if(!world.isRemote){
			if(inputSlot.isItemEqual(Items.REDSTONE.getDefaultInstance()) || inputSlot.isItemEqual(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK).getDefaultInstance()) && this.energy.getEnergyStored() >= energyPerTick){
				if(FLUID_PER_REDSTONE * (isBlock ? 10 : 1) == fluids.internalFill(new FluidStack(ModFluids.LIQUID_REDSTONE, FLUID_PER_REDSTONE * (isBlock ? 10 : 1)), false)){
					if(energy.internalExtract(energyPerTick * (isBlock ? 12 : 1), true) == energyPerTick * (isBlock ? 12 : 1)){
						fluids.internalFill(new FluidStack(ModFluids.LIQUID_REDSTONE, FLUID_PER_REDSTONE * (isBlock ? 10 : 1)), true);
						energy.internalExtract(energyPerTick * (isBlock ? 12 : 1), false);
						this.inventory.get(INPUT_SLOT.ordinal()).shrink(1);
						setLiquefying(true);
						flag = true;
						if(this.inventory.get(INPUT_SLOT.ordinal()).isEmpty())inventory.set(INPUT_SLOT.ordinal(), ItemStack.EMPTY);
					}
				}
			}else setLiquefying(false);

			if(fluids.getFluidAmount() > 0){
				for(EnumFacing f : EnumFacing.values()){
					if(fluids.getFluidAmount() == 0) break;
					TileEntity te = world.getTileEntity(pos.offset(f));
					if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite())){
						IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, f.getOpposite());
						if(handler != null) {
							int extracted = Objects.requireNonNull(fluids.drain(fluids.getMaxDrain(), false)).amount,
									received = handler.fill(new FluidStack(ModFluids.LIQUID_REDSTONE, extracted), false);
							received = handler.fill(new FluidStack(ModFluids.LIQUID_REDSTONE, Math.min(extracted, received)), true);
							if (received > 0) {
								fluids.drain(extracted, true);
								flag = true;
							}
						}
					}
				}
			}
		}
		if(flag) markDirty();
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity">

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY) {
			return (T) energy;
		}
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) fluids;
		}
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

//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable">

	@Override
	public EnumUpgradeTier getCurrentTier() { return this.tier; }

	@Override
	public void setTier(EnumUpgradeTier tier) { this.tier = tier;}

	public void updateTierRelatedComponents(){ updateTierRelatedComponents(energy.getEnergyStored(), fluids.getFluidAmount()); }

	private void updateTierRelatedComponents(int energyStored, int fluidHeld){
		fluids = tier.getFluidTier().getFluidTank(new FluidStack(ModFluids.LIQUID_REDSTONE, fluidHeld), false);
		energy = tier.getEnergyTier().getEnergyStorage(energyStored);
		this.energyPerTick = tier.getEnergyTier().getRequired();
	}

//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Lockable">

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.inventory.set(index, stack);
		if(stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) { return stack.isItemEqual(Items.REDSTONE.getDefaultInstance())|| stack.isItemEqual(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK).getDefaultInstance()); }

	@Override
	public int getField(int id) {
		switch(id){
			case 0: return this.fluids.getFluidAmount();
			case 1: return this.energy.getEnergyStored();
			case 2: return this.fluids.getCapacity();
			case 3: return this.energy.getMaxEnergyStored();
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
				this.energy.setCapacity(value);
				break;
		}
	}

	@Override
	public int getFieldCount(){ return 5; }

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) { return new ContainerLiquefier(playerInventory, this); }

	@Override
	public String getGuiID() { return "lavasources:liquefier"; }
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Sided Inventory">
	@Override
	public int[] getSlotsForFace(EnumFacing side) { return SLOTS; }

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if(index == INPUT_SLOT.ordinal()) return itemStackIn.getItem() == ModItems.EMPTY_CORE;
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) { return stack.getItem() != ModItems.EMPTY_CORE; }
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Persistent Inventory">
	@Override
	public boolean isDestroyedByCreative() { return destroyedByCreative; }

	@Override
	public void setDestroyedByCreative(boolean destroyedByCreative) { this.destroyedByCreative = destroyedByCreative; }

	@Override
	public boolean shouldDropSpecial() { return fluids.getFluidAmount() > 0 || energy.getEnergyStored() > 0 || this.inventory.get(INPUT_SLOT.ordinal()) != ItemStack.EMPTY || this.tier.LEVEL > 0; }

	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound) {
		compound.setInteger(TIER_TAG, tier.LEVEL);
		compound.setTag(ENERGY_TAG, energy.serializeNBT());
		compound.setTag(FLUID_TAG, fluids.serializeNBT());
		compound.setBoolean(LIQUEFYING_TAG, isLiquefying());


		return compound;
	}

	@Override
	public void readItem(NBTTagCompound compound){
		tier = EnumUpgradeTier.values()[compound.getInteger(TIER_TAG)];
		this.updateTierRelatedComponents();
		if(compound.hasKey(ENERGY_TAG)) energy.deserializeNBT(compound.getCompoundTag(ENERGY_TAG));
		if(compound.hasKey(FLUID_TAG)) fluids.deserializeNBT(compound.getCompoundTag(FLUID_TAG));
		if(compound.hasKey(LIQUEFYING_TAG)) setLiquefying(compound.getBoolean(LIQUEFYING_TAG));
	}

//</editor-fold>

}
