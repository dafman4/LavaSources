package squedgy.lavasources.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
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
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.enums.EnumGuiElements;
import squedgy.lavasources.generic.ModPersistentBlock;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.tileentity.TileEntityCoreModifier;

/**
 *
 * @author David
 */
public class BlockCoreModifier extends ModPersistentBlock {
	
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool MAKING = PropertyBool.create("powered");
	
//<editor-fold desc="Constructors" defaultstate="collapsed">
	public BlockCoreModifier(){
		this("core_modifier");
	}
	
	public BlockCoreModifier(String unlocName){
		super(unlocName, ModBlocks.CORE_MODIFIER);
		this.setDefaultState(this.getDefaultState()
			.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(MAKING, false)
		);
	}
//</editor-fold>
	
//<editor-fold desc="Block Overrides" defaultstate="collapsed">

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityCoreModifier entity = new TileEntityCoreModifier();
		entity.setFacing(this.getStateFromMeta(meta).getValue(FACING));
		return entity;
	}
	@Override
	public IProperty[] getProperties(){
		return new IProperty[] {FACING , MAKING};
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(MAKING) ? 1 : 0) << 2 | state.getValue(FACING).getHorizontalIndex();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta - 1 << 2);
		boolean powered = meta >> 2 == 1;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(MAKING, powered);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		((TileEntityCoreModifier)worldIn.getTileEntity(pos)).setFacing(placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityCoreModifier te = (TileEntityCoreModifier) worldIn.getTileEntity(pos);
		if(te!= null && te.getFacing() != null) return state.withProperty(MAKING, te.isMaking()).withProperty(FACING, te.getFacing());
		else return state;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote){
			if(playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD){
				TileEntityCoreModifier cable = (TileEntityCoreModifier) worldIn.getTileEntity(pos);
				if(cable !=null){
					cable.upgrade(cable.getTier().getUpgrade());
				}
			}else{
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof TileEntityCoreModifier){
					playerIn.openGui(LavaSources.INSTANCE, EnumGuiElements.CORE_MODIFIER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		return true;
	}

//</editor-fold>

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return TileEntityCoreModifier.class;
	}

}
