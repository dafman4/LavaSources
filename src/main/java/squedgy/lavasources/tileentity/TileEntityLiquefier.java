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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.crafting.recipes.LiquefierRecipe;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.recipes.ILiquefierRecipe;
import squedgy.lavasources.generic.tileentities.IPersistentInventory;
import squedgy.lavasources.generic.tileentities.IUpgradeable;
import squedgy.lavasources.init.ModFluids;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.inventory.ContainerLiquefier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static squedgy.lavasources.tileentity.TileEntityLiquefier.SlotEnum.INPUT_SLOT;

/**
 *
 * @author David
 */
public class TileEntityLiquefier extends ModLockableTileEntity implements IUpgradeable, ITickable, ISidedInventory, IPersistentInventory {

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">
	public static final String TIER_TAG = "tier", ENERGY_TAG = "energy", FLUID_TAG = "fluid", LIQUEFYING_TAG = "liquefying", INVENTORY_TAG = "inventory";
	public enum SlotEnum{ INPUT_SLOT }
	private static final int[] SLOTS = {INPUT_SLOT.ordinal()};
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	private static List<ILiquefierRecipe> recipes;
	private static final ILiquefierRecipe BLANK_RECIPE = new LiquefierRecipe("empty_recipe", null, null, 0);
	private ILiquefierRecipe recipe = BLANK_RECIPE;
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
	
	public boolean isLiquefying(){ return liquefying; }

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
		if(!world.isRemote){
			ItemStack input = inventory.get(INPUT_SLOT.ordinal());
			boolean liquefying = false;
			if(!recipe.hasInput(input)){
				recipe = recipes.stream().filter(r -> r.hasInput(input)).findFirst().orElse(BLANK_RECIPE);
			}
			if(recipe != BLANK_RECIPE && this.energy.getEnergyStored() >= energyPerTick && (fluids.getCapacity() - fluids.getFluidAmount()) >= recipe.getOutput(input).amount){
				FluidStack output = recipe.getOutput(input);
				LavaSources.writeMessage(getClass(), "output = [" + output.getFluid().getName() + ", " + output.amount + "]");
				int energyExtract = energy.internalExtract(energyPerTick, true), fluidExtract = fluids.internalFill(output, false);
				LavaSources.writeMessage(getClass(), "energyExtract = " + energyExtract + " , " + energyPerTick + ", fluidExtract = " + fluidExtract);
				if(energyExtract == energyPerTick && fluidExtract == output.amount){
					energy.internalExtract(energyPerTick, false);
					fluids.internalFill(output, true);
					flag = true;
					liquefying = true;
					input.shrink(1);
					if(input.isEmpty())inventory.set(INPUT_SLOT.ordinal(), ItemStack.EMPTY);
				}
			}
			setLiquefying(liquefying);
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
								fluids.drain(received, true);
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
		fluids = tier.getFluidTier().getFluidTank(new FluidStack(ModFluids.LIQUID_REDSTONE, fluidHeld), recipes.stream().map(r -> r.getOutput(r.getInputs()[0])).toArray(FluidStack[]::new));
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
		if(compound.hasKey(LIQUEFYING_TAG)) liquefying = (compound.getBoolean(LIQUEFYING_TAG));
	}

//</editor-fold>

	public static void updateRecipes(){
		recipes = new ArrayList<>(GameRegistry.findRegistry(ILiquefierRecipe.class).getValuesCollection());
	}

}
