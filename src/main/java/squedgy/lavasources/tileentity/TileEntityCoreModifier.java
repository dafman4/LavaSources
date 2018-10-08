package squedgy.lavasources.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.generic.ModLockableTileEntity;
import squedgy.lavasources.helper.EnumConversions;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.ContainerCoreModifier;

import java.util.*;
import java.util.stream.IntStream;

/**
 *
 * @author David
 */
public class TileEntityCoreModifier extends ModLockableTileEntity implements IUpgradeable, ITickable, ISidedInventory, IPersistentInventory {
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields">
	public enum SlotEnum{ INPUT_SLOT, OUTPUT_SLOT }
	public enum EnumMaking{ ENERGY_CORE(ModItems.ENERGY_CORE), LAVA_CORE(ModItems.LAVA_CORE);
		private final Item RETURN_ITEM;

		EnumMaking(Item returnItem){RETURN_ITEM = returnItem;}

		public Item getItem(){return RETURN_ITEM;}
	}
	public static final List<FluidStack> POSSIBLE_FLUIDS = new ArrayList(Arrays.asList(
			new FluidStack(FluidRegistry.LAVA, 0),
			new FluidStack(ModFluids.LIQUID_REDSTONE, 0)
	));
    private static final String FLUIDS_TAG = "fluids", TIER_TAG = "tier", ENERGY_TAG = "energy", TICKS_TAG = "ticks", MAKING_TAG = "making";
	private static final int[] SLOTS = {SlotEnum.INPUT_SLOT.ordinal(), SlotEnum.OUTPUT_SLOT.ordinal()};
	public  static final int FILL_TIME = EnumConversions.SECONDS_TO_TICKS.convertToInt(20);
	private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
	private EnumMaking making = null;
	private int ticksFilling, energyPerTick, fluidPerTick;
	private EnumUpgradeTier tier;
	private ModEnergyStorage energy;
	private ModFluidTank fluids;
	private EnumFacing facing;
	protected boolean destroyedByCreative;
	private int makingFluidAmount = 0;
//</editor-fold>	

//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	public TileEntityCoreModifier(EnumUpgradeTier tier){
		super("core_modifier");
		this.tier = tier;
		this.updateTierRelatedComponents(0,
				FluidRegistry.getFluidStack(FluidRegistry.LAVA.getName(), 0),
				POSSIBLE_FLUIDS.stream().filter((fluid) -> !fluid.isFluidEqual(FluidRegistry.getFluidStack(FluidRegistry.LAVA.getName(), 0))).toArray(FluidStack[]::new)
		);
        LavaSources.writeMessage(getClass(), "Fill time = " + FILL_TIME + " ticks");
	}
	
	public TileEntityCoreModifier(){
		this(EnumUpgradeTier.BASIC);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public EnumUpgradeTier getTier() { return tier; }

	public EnumMaking getMaking() { return making; }

	public ModFluidTank getFluids() { return fluids; }

	public void setFacing(EnumFacing facing){
		boolean test = false;
		for(EnumFacing face : EnumFacing.HORIZONTALS)
			test = test | facing == face;
		if(test)
			this.facing = facing;
	}
	
	public EnumFacing getFacing(){ return facing; }
	
	public ModEnergyStorage getEnergy() { return energy; }
	
	public NonNullList<ItemStack> getInventory(){ return this.inventory; }

	public boolean isMaking(){ return making != null; }
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	
	@Override
	public void update() {
		boolean markdirty = false, flagMaking = isMaking();
        if(this.making != null && ticksFilling < FILL_TIME && Objects.requireNonNull(this.fluids.drain(fluidPerTick, false)).amount >= this.fluidPerTick && this.energy.extractEnergy(energyPerTick, true) >= this.energyPerTick){
            int drained =  Objects.requireNonNull(this.fluids.drain(fluidPerTick, true)).amount;
            makingFluidAmount += drained;
            this.energy.extractEnergy(energyPerTick, false);
            this.ticksFilling++;
        }else if(making == null && ticksFilling > 0){
            this.ticksFilling = 0;
            this.makingFluidAmount = 0;
        }
		if(!world.isRemote){
			if(this.making == null){
				if(this.ticksFilling > 0){
					this.ticksFilling = 0;
					markdirty = true;
				}
				ItemStack stack = this.inventory.get(SlotEnum.INPUT_SLOT.ordinal());
				if(stack.getItem() == ModItems.EMPTY_CORE){
					if(this.fluids.getFluid() != null && this.fluids.getInfoWrapper().getAmountStored() > 0){
                        if(this.fluids.getFluid().isFluidEqual(new FluidStack(FluidRegistry.LAVA, 0))){
							making = EnumMaking.LAVA_CORE;
						}else if(this.fluids.getFluid().getFluid() == ModFluids.LIQUID_REDSTONE){
							making = EnumMaking.ENERGY_CORE;
						}
						markdirty = true;
					}
				}
			}else if(this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).getItem() != ModItems.EMPTY_CORE){
				this.making = null;
				this.ticksFilling = 0;
			}else if(this.ticksFilling >= FILL_TIME){
				if(this.inventory.get(SlotEnum.OUTPUT_SLOT.ordinal()).isEmpty()){
					this.inventory.set(SlotEnum.OUTPUT_SLOT.ordinal(), new ItemStack(making.getItem(), 1));
					this.inventory.get(SlotEnum.INPUT_SLOT.ordinal()).shrink(1);
					LavaSources.writeMessage(getClass(), "Making Fluid Amount = " + makingFluidAmount);
					this.ticksFilling = 0;
					this.makingFluidAmount = 0;
					this.making = null;
					markdirty = true;
				}
			}
		}
		if(markdirty) markDirty();
		if(flagMaking != isMaking())notifyBlockUpdate();
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity Override">

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
	public EnumUpgradeTier getCurrentTier() { return this.tier; }

	@Override
	public void setTier(EnumUpgradeTier tier) { this.tier = tier; }

//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Lockable Overrides">

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack itemStack = inventory.get(index);
		boolean flag  = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
		this.inventory.set(index, stack);
		if(stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
		
		if(index == TileEntityCoreModifier.SlotEnum.INPUT_SLOT.ordinal() && !flag){
			this.ticksFilling = 0;
			markDirty();
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == SlotEnum.OUTPUT_SLOT.ordinal()) return false;
		else if(index != SlotEnum.INPUT_SLOT.ordinal()&& stack.getItem() == ModItems.EMPTY_CORE) return true;
		else return stack.getItem() == ModItems.EMPTY_CORE;
	}

	@Override
	public int getField(int id) {
		switch(id){
			case 0: return this.ticksFilling;
			case 1: return this.fluids.getFluidAmount();
			case 2: return this.energy.getEnergyStored();
			case 3:
				OptionalInt index = IntStream.range(0, POSSIBLE_FLUIDS.size()).filter((i) -> POSSIBLE_FLUIDS.get(i).isFluidEqual(fluids.getFluid())).findFirst();
				return index.isPresent() ? index.getAsInt() : -1;
			case 4: return this.fluids.getCapacity();
			case 5: return this.energy.getMaxEnergyStored();
			case 6: return making != null ? this.making.ordinal() : -1;
			case 7: return FILL_TIME;
			default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id){
			case 0:
				this.ticksFilling = value;
				break;
			case 1:
				this.fluids.setFluidAmount(value);
				break;
			case 2:
				this.energy.setPowerStored(value);
				break;
			case 3:
				fluids.setFluidType(new FluidStack(POSSIBLE_FLUIDS.get(value), 0));
				break;
			case 4:
				this.fluids.setCapacity(value);
				break;
			case 5:
				this.energy.setCapacity(value);
				break;
			case 6:
				if(value > -1) making = EnumMaking.values()[value];
				else making = null;
				break;
		}
	}

	@Override
	public int getFieldCount(){
		return 7;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerCoreModifier(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return "lavasources:core_modifier";
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Sided Inventory Overrides">
	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return index == SlotEnum.INPUT_SLOT.ordinal() && itemStackIn.getItem() == ModItems.EMPTY_CORE;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return index == SlotEnum.OUTPUT_SLOT.ordinal();
	}
	
//</editor-fold>
	
//<editor-fold defaultstate=collapsed desc=". . . . Persistent Inventory">
	public boolean shouldDropSpecial(){
		return inventory.stream().anyMatch((s) -> !s.isEmpty())
				|| this.hasCustomName()
				|| energy.getEnergyStored() > 0
				|| (fluids.getFluid() != null && fluids.getFluid().amount > 0)
				|| this.tier.LEVEL > 0;
	}

	@Override
	public boolean isDestroyedByCreative() { return destroyedByCreative; }

	@Override
	public void setDestroyedByCreative(boolean destroyedByCreative) { this.destroyedByCreative = destroyedByCreative; }

	@Override
	public NBTTagCompound writeItem(NBTTagCompound compound){
		compound.setInteger(TIER_TAG, tier.LEVEL);
		compound.setTag(ENERGY_TAG, energy.serializeNBT());
		compound.setTag(FLUIDS_TAG, fluids.serializeNBT());
		compound.setInteger(TICKS_TAG, this.ticksFilling);
		compound.setInteger(MAKING_TAG, making != null ? making.ordinal() : -1);
		return compound;
	}

	@Override
	public void readItem(NBTTagCompound compound) {
		if(compound.hasKey(TIER_TAG)) tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		updateTierRelatedComponents();
		if(compound.hasKey(ENERGY_TAG))energy.deserializeNBT(compound.getCompoundTag(ENERGY_TAG));
		if(compound.hasKey(FLUIDS_TAG))fluids.deserializeNBT(compound.getCompoundTag(FLUIDS_TAG));
		if(compound.hasKey(TICKS_TAG)) ticksFilling = compound.getInteger(TICKS_TAG);
		int making = this.making!= null? this.making.ordinal(): -1;
		if(compound.hasKey(MAKING_TAG)) making = compound.getInteger(MAKING_TAG);
		if(making > -1)
			this.making = EnumMaking.values()[making];
		else
			this.making = null;
	}

	@Override
	public String toString() {
		return super.toString() + "\nTileEntityCoreModifier{x:" + pos.getX()+ ", y:" + pos.getY() + ", z:" + pos.getZ()
				+ "inventory:" + inventory.toString()+", making" + making + ", ticksFilling + " + ticksFilling
				+ ", energyPerTick:" + energyPerTick + ", fluidPerTick:" + fluidPerTick + ", tier:" + tier
				+ ", energy:" + energy + ", fluids:"+ fluids + ", facing:"+facing+"}";
	}
//</editor-fold>

//<editor-fold defaultstate=collapsed desc=". . . . Helpers">

	public void updateTierRelatedComponents(){
		this.updateTierRelatedComponents(energy.getEnergyStored(),
				fluids.getFluid(),
				POSSIBLE_FLUIDS.stream().filter((fluid) -> !fluid.isFluidEqual(fluids.getFluid())).toArray(FluidStack[]::new)
		);
	}

	private void updateTierRelatedComponents(int energyStored, FluidStack contained, FluidStack[] extraFluids){
		energy = tier.getEnergyTier().getEnergyStorage(energyStored);
		fluids = tier.getFluidTier().getFluidTank(contained, extraFluids);
		energyPerTick = tier.getEnergyTier().REQUIRED;
		fluidPerTick = tier.getFluidTier().REQUIRED;
		markDirty();
	}

	public static void addPossibleFluid(FluidStack newFluid){
		if(POSSIBLE_FLUIDS.stream().noneMatch((fluid) -> fluid.isFluidEqual(newFluid))) POSSIBLE_FLUIDS.add(newFluid);
	}

//</editor-fold>
}
