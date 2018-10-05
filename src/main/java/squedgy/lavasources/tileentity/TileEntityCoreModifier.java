package squedgy.lavasources.tileentity;

import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.FluidHandler;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.helper.EnumConversions;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.ContainerCoreModifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author David
 */
public class TileEntityCoreModifier extends TileEntityLockable implements IUpgradeable, ITickable, ISidedInventory, IPersistentInventory { 
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields">
	public enum SlotEnum{ INPUT_SLOT, OUTPUT_SLOT }
	public enum EnumMaking{ ENERGY_CORE(ModItems.ENERGY_CORE), LAVA_CORE(ModItems.LAVA_CORE);
		private final Item RETURN_ITEM;

		EnumMaking(Item returnItem){RETURN_ITEM = returnItem;}

		public Item getItem(){return RETURN_ITEM;}
	}
	public static final List<FluidStack> POSSIBLE_FLUIDS = Arrays.asList(
			new FluidStack(FluidRegistry.LAVA, 0),
			new FluidStack(ModFluids.LIQUID_REDSTONE, 0)
	);
    private static final String FLUID_AMOUNT_TAG = "fluid_amount", FLUID_INDEX_TAG = "fluid_index", TIER_TAG = "tier", ENERGY_STORED_TAG = "energy_stored", TICKS_TAG = "ticks", MAKING_TAG = "making";
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
		this.tier = tier;
		this.updateTierRelatedComponents(0, new FluidStack(FluidRegistry.LAVA, 0), new FluidStack[] {new FluidStack(ModFluids.LIQUID_REDSTONE, 0)});
        LavaSources.writeMessage(getClass(), "Fill time = " + FILL_TIME + " ticks");
	}
	
	public TileEntityCoreModifier(){
		this(EnumUpgradeTier.BASIC);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">
	public EnumUpgradeTier getTier() { return tier; }

	public EnumMaking getMaking() {
		return making;
	}

	public ModFluidTank getFluids() {
		return fluids;
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

	public boolean isPowered(){
		return energy.getEnergyStored() > 0;
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . ITickable">
	
	@Override
	public void update() {
        if(this.making != null && ticksFilling < FILL_TIME && Objects.requireNonNull(this.fluids.drain(fluidPerTick, false)).amount >= this.fluidPerTick && this.energy.extractEnergy(energyPerTick, true) >= this.energyPerTick){
            int drained =  Objects.requireNonNull(this.fluids.drain(fluidPerTick, true)).amount;
            makingFluidAmount += drained;
            this.energy.extractEnergy(energyPerTick, false);
            this.ticksFilling++;
        }else if(making == null && ticksFilling > 0){
            this.ticksFilling = 0;
            this.makingFluidAmount = 0;
        }
		boolean markDirty = false;
		if(!world.isRemote){
			if(this.making == null){
				if(this.ticksFilling > 0){
					this.ticksFilling = 0;
					markDirty = true;
				}
				ItemStack stack = this.inventory.get(SlotEnum.INPUT_SLOT.ordinal());
				if(stack.getItem() == ModItems.EMPTY_CORE){
					if(this.fluids.getFluid() != null && this.fluids.getInfoWrapper().getAmountStored() > 0){
                        if(this.fluids.getFluid().isFluidEqual(new FluidStack(FluidRegistry.LAVA, 0))){
							making = EnumMaking.LAVA_CORE;
						}else if(this.fluids.getFluid().getFluid() == ModFluids.LIQUID_REDSTONE){
							making = EnumMaking.ENERGY_CORE;
						}
						markDirty = true;
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
					markDirty = true;
				}
			}
		}
		if(markDirty) markDirty();
	}
	
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
		int fluidIndex = 0, fluidAmount = 0;
		if(fluids.getFluid() != null){
		    fluidIndex = fluids.getFluid().isFluidEqual(new FluidStack(POSSIBLE_FLUIDS.get(0), 0)) ? 0 : 1;
		    fluidAmount = fluids.getFluidAmount();
		}
        int energyStored = energy.getEnergyStored();
        if(compound.hasKey(TIER_TAG)) tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		if(compound.hasKey(FLUID_INDEX_TAG) && compound.hasKey(FLUID_AMOUNT_TAG)) {
		    fluidIndex = compound.getInteger(FLUID_INDEX_TAG);
		    fluidAmount = compound.getInteger(FLUID_AMOUNT_TAG);
		}
		if(compound.hasKey(ENERGY_STORED_TAG)) energyStored = (compound.getInteger(ENERGY_STORED_TAG));
		updateTierRelatedComponents(energyStored,
            new FluidStack(POSSIBLE_FLUIDS.get(fluidIndex), fluidAmount),
            new FluidStack[] {new FluidStack(POSSIBLE_FLUIDS.get(fluidIndex^1), 0)}
        );
		if(compound.hasKey(TICKS_TAG)) ticksFilling = compound.getInteger(TICKS_TAG);
		ItemStackHelper.loadAllItems(compound, this.inventory);
		int making = this.making!= null? this.making.ordinal(): -1;
		if(compound.hasKey(MAKING_TAG)) making = compound.getInteger(MAKING_TAG);
		if(making > -1)
			this.making = EnumMaking.values()[making];
		else
			this.making = null;
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
        LavaSources.writeMessage(getClass(), "trying to upgrade using " + tier);
		if(EnumUpgradeTier.isUpgradeFor(this, tier)){
			this.tier = this.tier.getUpgrade();
			this.updateTierRelatedComponents();
			ret = true;
			markDirty();
		}
		return ret;
	}

	@Override
	public EnumUpgradeTier getCurrentTier() {
		return this.tier;
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Lockable Overrides">

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

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
	public ItemStack getStackInSlot(int index) {
		return this.inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.inventory, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

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
	public int getInventoryStackLimit() { return 64; }

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
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index == SlotEnum.OUTPUT_SLOT.ordinal()) return false;
		else if(index != SlotEnum.INPUT_SLOT.ordinal()&& stack.getItem() == ModItems.EMPTY_CORE) return true;
		else return stack.getItem() == ModItems.EMPTY_CORE;
	}

	@Override
	public int getField(int id) {
		switch(id){
			case 0: return this.ticksFilling;
			case 1: return this.fluids.getInfoWrapper().getAmountStored();
			case 2: return this.energy.getEnergyStored();
			case 3: return POSSIBLE_FLUIDS.get(0).isFluidEqual(fluids.getFluid()) ? 0 : 1;
			case 4: return this.fluids.getCapacity();
			case 5: return this.energy.getMaxEnergyStored();
			case 6: return making != null ? this.making.ordinal() : -1;
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
				this.energy.setMaxPowerStored(value);
				break;
			case 6:
				if(value > -1){
					making = EnumMaking.values()[value];
				}else{
					making = null;
				}
				break;
		}
	}

	@Override
	public int getFieldCount(){
		return 7;
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public String getName() {
		return "container.core_modifier";
	}

	@Override
	public boolean hasCustomName() {
		return false;
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
		return this.inventory.get(0) != ItemStack.EMPTY
				|| this.inventory.get(1) != ItemStack.EMPTY
				|| this.hasCustomName()
				|| energy.getEnergyStored() > 0
				|| (fluids.getFluid() != null && fluids.getFluid().amount > 0)
				|| this.tier.LEVEL > 0;
	}

	@Override
	public boolean isDestroyedByCreative() {
		return destroyedByCreative;
	}

	@Override
	public void setDestroyedByCreative(boolean destroyedByCreative) {
		this.destroyedByCreative = destroyedByCreative;
	}

	@Override
	public String toString() {
		return super.toString() + "\nTileEntityCoreModifier{x:" + pos.getX()+ ", y:" + pos.getY() + ", z:" + pos.getZ()
				+ "inventory:" + inventory.toString()+", making" + making + ", ticksFilling + " + ticksFilling
				+ ", energyPerTick:" + energyPerTick + ", fluidPerTick:" + fluidPerTick + ", tier:" + tier
				+ ", energy:" + energy + ", fluids:"+ fluids + ", facing:"+facing+"}";
	}

    @Override
    public NBTTagCompound writeItem(NBTTagCompound compound){
        compound.setInteger(TIER_TAG, tier.LEVEL);
        compound.setInteger(ENERGY_STORED_TAG, energy.getPowerStored());
        compound.setInteger(FLUID_AMOUNT_TAG, fluids.getFluidAmount());
        compound.setInteger(TICKS_TAG, this.ticksFilling);
        compound.setInteger(FLUID_INDEX_TAG, POSSIBLE_FLUIDS.get(0).isFluidEqual(fluids.getFluid())? 0 : 1);
        compound.setInteger(MAKING_TAG, making != null ? making.ordinal() : -1);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        return compound;
    }
//</editor-fold>

//<editor-fold defaultstate=collapsed desc=". . . . Helpers">

	private void updateTierRelatedComponents(){
	    if(fluids.getFluid() != null)
		this.updateTierRelatedComponents(energy.getEnergyStored(),
				fluids.getFluid(),
				new FluidStack[] {new FluidStack(fluids.getFluid().isFluidEqual(new FluidStack(FluidRegistry.LAVA, 0)) ? ModFluids.LIQUID_REDSTONE : FluidRegistry.LAVA, 0)}
		);
	    else updateTierRelatedComponents(energy.getEnergyStored(), new FluidStack(FluidRegistry.LAVA, fluids.getFluidAmount()), new FluidStack[] {new FluidStack(ModFluids.LIQUID_REDSTONE,0)});
	}

	private void updateTierRelatedComponents(int energyStored, FluidStack contained, FluidStack[] extraFluids){
		energy = tier.getEnergyTier().getEnergyStorage(energyStored);
		fluids = tier.getFluidTier().getFluidTank(contained, extraFluids);
		energyPerTick = tier.getEnergyTier().REQUIRED;
		fluidPerTick = tier.getFluidTier().REQUIRED;
	}

//</editor-fold>
}
