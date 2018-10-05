package squedgy.lavasources.tileentity;

import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.block.BlockEnergyGenerator;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IPersistentInventory;
import squedgy.lavasources.generic.IUpgradeable;
import squedgy.lavasources.init.ModFluids;


/**
 *
 * @author David
 */
public class TileEntityEnergyGenerator extends TileEntity implements ITickable, IUpgradeable, IPersistentInventory {
	
//<editor-fold defaultstate="collapsed" desc=". . . . Fields">

    protected ModEnergyStorage energy;
	protected EnumFacing facing;
	protected EnumUpgradeTier tier;
	protected int generated;
	protected boolean destroyedByCreative;
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Constructors">
	
	public TileEntityEnergyGenerator(EnumUpgradeTier tier){
		this.tier = tier;
		updateTierRelatedComponents(0, EnumFacing.NORTH);
        energy.receive = false;
	}
	
	
	public TileEntityEnergyGenerator(){
		this(EnumUpgradeTier.BASIC);
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . ITickable/helper methods">
	@Override
	public void update() { 
		if(this.getPowered()){
			energy.receive = true;
			energy.receiveEnergy(generated, false);
			energy.receive = false;
		}
		int pushable = Math.min(energy.getMaxExtracted(), energy.getEnergyStored());
		if(pushable > 0){
			for(EnumFacing testFacing : EnumFacing.values()){
				TileEntity test = getWorld().getTileEntity(getPos().offset(testFacing));
				if(test != null){
					IEnergyStorage handler = test.getCapability(CapabilityEnergy.ENERGY, testFacing.getOpposite());
					if(handler != null && handler.canReceive()){
						pushable -= handler.receiveEnergy(pushable, false);
					}
				}
				if(pushable == 0) break;
			}
		}
	}

	public boolean getPowered() {
		return getWorld().isBlockPowered(getPos());
	}

	public EnumFacing getFacing() {
		return this.facing;
	}
	
	public void setFacing(EnumFacing facing){
		this.facing = facing;
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . TileEntity overrides">
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt ){
		readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
		NBTTagCompound compound = new NBTTagCompound();
		writeToNBT(compound);
		return new SPacketUpdateTileEntity(getPos(), 1, compound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return writeToNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return writeItem(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound); //To change body of generated methods, choose Tools | Templates.
		tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		updateTierRelatedComponents();
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return (T) energy;
		return super.getCapability(capability, facing); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY)
			return true;
		return super.hasCapability(capability, facing);
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . IUpgradeable Overrides">
	@Override
	public boolean upgrade(EnumUpgradeTier tier) {
		boolean ret = false;
		if(tier.LEVEL > this.tier.LEVEL){
			this.energy = tier.getEnergyTier().getEnergyStorage((energy != null) ? energy.getPowerStored() : 0);
			energy.receive = false;
			this.generated = tier.getEnergyTier().getGenerated();
			this.tier = tier;
			ret = true;
			markDirty();
		}
		return ret;
	}

	@Override
	public EnumUpgradeTier getCurrentTier() {
		return tier;
	}
	
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Persistent Inventory">

    @Override
    public boolean shouldDropSpecial() {
        return this.energy.getEnergyStored() > 0 || this.tier.LEVEL > 0;
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
    public NBTTagCompound writeItem(NBTTagCompound compound) {
        compound.setInteger("tier", tier.LEVEL);
        compound.setInteger("stored", energy.getPowerStored());
        compound.setInteger("facing", facing.getHorizontalIndex());
        return compound;
    }

//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc=". . . . Helpers">
	
	public EnumUpgradeTier getTier(){ return tier; }

    private void updateTierRelatedComponents(){ updateTierRelatedComponents( energy.getEnergyStored(), facing ); }

    private void updateTierRelatedComponents(int energyStored, EnumFacing facing){
        this.energy = this.tier.getEnergyTier().getEnergyStorage(energyStored);
        this.generated = this.tier.getEnergyTier().GENERATED;
        this.facing = facing;
    }

//</editor-fold>
	
}
