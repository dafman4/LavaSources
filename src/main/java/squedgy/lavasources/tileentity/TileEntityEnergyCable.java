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
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.block.BlockEnergyCable;
import squedgy.lavasources.capabilities.ModEnergyStorage;
import squedgy.lavasources.enums.EnumCableTier;
import squedgy.lavasources.enums.EnumUpgradeTier;
import squedgy.lavasources.generic.IUpgradeable;
/**
 *
 * @author David
 */
public class TileEntityEnergyCable extends TileEntity implements ITickable, IUpgradeable{

//<editor-fold defaultstate="collapsed" desc="Fields">
	private EnumUpgradeTier tier;
	private ModEnergyStorage energy;
	private boolean powered;
	private boolean[] receivedFrom = new boolean[EnumFacing.values().length];
//</editor-fold>	

//<editor-fold defaultstate="collapsed" desc="Constructors">
	public TileEntityEnergyCable(EnumUpgradeTier tier, boolean powered){
		this.tier = tier;
		energy = EnumCableTier.getCableTier(tier).getEnergyStorage();
	}
	
	public TileEntityEnergyCable(boolean powered){
		this(EnumUpgradeTier.BASIC, powered);
	}
	
	public TileEntityEnergyCable(){
		this(false);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Getters/Setters">
	public EnumUpgradeTier getTier() {
		return tier;
	}

	public ModEnergyStorage getEnergy() {
		return energy;
	}
	
	public void setReceivedFrom(EnumFacing f){
		this.receivedFrom[f.ordinal()] = true;
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="ITickable">
	@Override
	public void update() {
		if(energy.getEnergyStored() > 0){
			if(!powered){
				powered = true;
				markDirty();
			}
			for(EnumFacing f : EnumFacing.values()){
				if(energy.getPowerStored() == 0) break;
				if(!receivedFrom[f.ordinal()]){
					TileEntity te = world.getTileEntity(pos.offset(f));
					if(te != null && te instanceof TileEntityEnergyCable){
						((TileEntityEnergyCable)te).setReceivedFrom(f.getOpposite());
					}
					if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())){
						transferEnergy(te, f);
					}
				}
			}
		}
		if(powered && energy.getEnergyStored() <= 0){
			powered = false;
			markDirty();
		}
		receivedFrom = new boolean[]{false,false,false,false,false,false};
	}
	
	private void transferEnergy(TileEntity te, EnumFacing f){
		IEnergyStorage en = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
		if(en != null && en.canReceive()){
			int extracted = energy.extractEnergy(energy.getMaxExtracted(), true), received = en.receiveEnergy(extracted, true);
			received = en.receiveEnergy(energy.extractEnergy(Math.min(extracted,received), true), false);
			if(received > 0){
				energy.extractEnergy(received, false);
			}
		}
	}
	
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc="TileEntity Override">
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
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt ){
		readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		boolean ret = super.shouldRefresh(world, pos, oldState, newState);
		
		if(oldState instanceof BlockEnergyCable && newState instanceof BlockEnergyCable){
			ret = ret | !Objects.equals(oldState.getValue(BlockEnergyCable.CHARGED), newState.getValue(BlockEnergyCable.CHARGED));
		}
		return ret;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("tier", tier.LEVEL);
		compound.setInteger("stored", energy.getPowerStored());
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound); //To change body of generated methods, choose Tools | Templates.
		tier = EnumUpgradeTier.values()[compound.getInteger("tier")];
		energy = EnumCableTier.getCableTier(tier).getEnergyStorage(compound.getInteger("stored"));
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY){
			return (T) energy;
		}
		return super.getCapability(capability, facing); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityEnergy.ENERGY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="IUpgradeable Overrides">
	@Override
	public boolean upgrade(EnumUpgradeTier tier) {
		boolean ret = false;
		if(EnumUpgradeTier.isUpgradeFor(this, tier)){
			this.tier = tier;
			this.energy = EnumCableTier.getCableTier(tier).getEnergyStorage(energy.getEnergyStored());
			ret = true;
		}
		return ret;
	}

	@Override
	public EnumUpgradeTier getCurrentTier() {
		return this.tier;
	}
//</editor-fold>
	
}
