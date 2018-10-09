package squedgy.lavasources.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.tileentity.TileEntityEnergyCable;

/**
 *
 * @author David
 */
public class BlockEnergyCable extends ModBlock {

//<editor-fold defaultstate="collapsed" desc="Fields">
	public static final PropertyBool CHARGED = PropertyBool.create("charged");
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc="Constructors">
	public BlockEnergyCable(){
		this("energy_cable");
	}
	
	public BlockEnergyCable(String unlocName){
		super(unlocName, ModBlocks.ENERGY_CABLE);
		this.setDefaultState(this.getDefaultState()
			.withProperty(CHARGED, false)
		);
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc="Block Overloads">
	@Override
	public boolean hasTileEntity(){
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityEnergyCable();
	}

	@Override
	public IProperty[] getProperties(){return new IProperty[] {CHARGED};}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) { worldIn.setBlockState(pos, state); }
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(CHARGED) ? 1 : 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		IBlockState ret = this.getDefaultState();
		ret.withProperty(CHARGED, (meta == 1));
		return ret;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityEnergyCable te = (TileEntityEnergyCable) worldIn.getTileEntity(pos);
		if(te!= null)
			return state.withProperty(CHARGED, te.getEnergy().getEnergyStored() > 0);
		else
			return state;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			if(playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD){
				TileEntityEnergyCable cable = (TileEntityEnergyCable) worldIn.getTileEntity(pos);
				if(cable !=null){
					return cable.upgrade(cable.getTier().getUpgrade());
				}
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}
	
//</editor-fold>

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return TileEntityEnergyCable.class;
	}

}
