package squedgy.lavasources.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.capabilities.IPlayerResearchCapability;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.init.ModCapabilities;
import squedgy.lavasources.init.ModItems;
import squedgy.lavasources.init.ModResearch;
import squedgy.lavasources.tileentity.TileEntityLavaSource;

import java.util.Objects;

/**
 *
 * @author David
 */
public class BlockLavaSource extends ModPersistentBlock{
	
//<editor-fold defaultstate="collapsed" desc="Constructors">
	public BlockLavaSource(){
		this("lava_source");
	}
	
	public BlockLavaSource(String name){
		super(name, ModBlocks.LAVA_SOURCE);
	}
//</editor-fold>
	
//<editor-fold defaultstate="collapsed" desc="Block Methods">
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TileEntityLavaSource();
	}

	@Override
	public IProperty[] getProperties(){ return new IProperty[] {}; }

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) { }

	@Override
	public int getMetaFromState(IBlockState ibs) { return 0; }

	@Override
	public IBlockState getStateFromMeta(int i) { return this.getDefaultState(); }

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		boolean ret = false;
		ModFluidTank lavaTank = ((TileEntityLavaSource) Objects.requireNonNull(worldIn.getTileEntity(pos))).getFluids();
		if(worldIn.isRemote){
			ret = (playerIn.inventory.getCurrentItem().getItem().equals(Items.BUCKET) && lavaTank != null && lavaTank.canFillBucket())
					|| playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD;
		}else if(playerIn.inventory.getCurrentItem().getItem().equals(Items.BUCKET)){
			if(lavaTank.canFillBucket()){
				if(lavaTank.fillBucket()){
					playerIn.addItemStackToInventory(Items.LAVA_BUCKET.getDefaultInstance());
					playerIn.inventory.getCurrentItem().shrink(1);
					ret = true;
				}
			}
		}else if(playerIn.inventory.getCurrentItem().getItem() == ModItems.UPGRADE_CARD){
			TileEntityLavaSource source = (TileEntityLavaSource) worldIn.getTileEntity(pos);
			ret = source.upgrade(source.getTier().getUpgrade());
		}
		if(playerIn.hasCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null)){
			IPlayerResearchCapability cap = playerIn.getCapability(ModCapabilities.PLAYER_RESEARCH_CAPABILITY, null);
			if(!cap.hasResearch(ModResearch.TEST)){
				cap.addResearch(ModResearch.TEST);;
			}else{;
			}
		}
		return ret;
	}

//</editor-fold>

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return TileEntityLavaSource.class;
	}

}
