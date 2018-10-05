package squedgy.lavasources.block;

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
import squedgy.lavasources.generic.ModPersistentBlock;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.tileentity.TileEntityEnergyGenerator;

/**
 * @author David
 */
public class BlockEnergyGenerator extends ModPersistentBlock {

//<editor-fold defaultstate="collapsed" desc="Fields">
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Constructors">
	public BlockEnergyGenerator() {
		this("energy_generator");
	}

	public BlockEnergyGenerator(String unlocalizedName) {
		super(unlocalizedName,ModBlocks.ENERGY_GENERATOR);
	}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Block Methods">
	@Override
	public boolean hasTileEntity() { return true; }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityEnergyGenerator entity = new TileEntityEnergyGenerator();
		entity.setFacing(this.getStateFromMeta(meta).getValue(FACING));
		return entity;
	}

	@Override
	public IProperty[] getProperties() { return new IProperty[] {FACING, POWERED}; }

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		((TileEntityEnergyGenerator) worldIn.getTileEntity(pos)).setFacing(placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public int getMetaFromState(IBlockState state) { return (state.getValue(POWERED) ? 1 : 0) << 2 | state.getValue(FACING).getHorizontalIndex(); }

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getHorizontal(meta - 1 << 2);
		boolean powered = meta >> 2 == 1;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntityEnergyGenerator te = (TileEntityEnergyGenerator) worldIn.getTileEntity(pos);
		if (te != null && te.getWorld() != null && te.getFacing() != null)
			return state.withProperty(POWERED, te.getWorld().isBlockPowered(pos)).withProperty(FACING, te.getFacing()); //To change body of generated methods, choose Tools | Templates.
		else
			return state;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		boolean ret = playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD;
		if (!worldIn.isRemote) {
			if (playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD) {
				TileEntityEnergyGenerator generator = (TileEntityEnergyGenerator) worldIn.getTileEntity(pos);
				if (generator != null) {
					generator.upgrade(generator.getTier().getUpgrade());
				}
			}
		}
		return ret;
	}
//</editor-fold>

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return TileEntityEnergyGenerator.class;
	}

}
