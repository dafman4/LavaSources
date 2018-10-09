package squedgy.lavasources.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import squedgy.lavasources.capabilities.ModFluidTank;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.tileentity.TileEntityLavaStation;

/**
 * This is going to essentially be the research station of the mod.
 * Researching lava related ideas, beliefs, and connections through this block(s).
 * May eventually be a multi-block structure
 */
public class BlockLavaStation extends ModBlock {
	private static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockLavaStation(){ this("lava_station"); }

	public BlockLavaStation(String unlocalizedName) {
		super(unlocalizedName, ModBlocks.LAVA_STATION);
	}

	@Override
	public boolean hasTileEntity() { return true; }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileEntityLavaStation(); }

	@Override
	public IProperty[] getProperties() { return new IProperty[] {FACING}; }

	@Override
	public int getMetaFromState(IBlockState state) { return state.getValue(FACING).getHorizontalIndex(); }

	@Override
	public IBlockState getStateFromMeta(int meta) { return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)); }

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
	    worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
        ((TileEntityLavaStation)worldIn.getTileEntity(pos)).setFacing(placer.getHorizontalFacing().getOpposite());
	}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	    boolean ret = false;
	    ItemStack item = playerIn.inventory.getCurrentItem();
	    TileEntity te = worldIn.getTileEntity(pos);
	    if(te instanceof TileEntityLavaStation){
            TileEntityLavaStation tileEntity = (TileEntityLavaStation)worldIn.getTileEntity(pos);
            ModFluidTank waterTank = tileEntity.getFluids().getFluidTank(new FluidStack(FluidRegistry.WATER, 0)),
                         lavaTank  = tileEntity.getFluids().getFluidTank(new FluidStack(FluidRegistry.LAVA , 0));
            if(worldIn.isRemote){
                ret = (item.isItemEqual(Items.WATER_BUCKET.getDefaultInstance()) && waterTank.getFluidAmount() < 1000)
                    || (item.isItemEqual(Items.LAVA_BUCKET .getDefaultInstance()) && lavaTank .getFluidAmount() < 1000);
            }else{
                if(item.isItemEqual(Items.WATER_BUCKET.getDefaultInstance())) if(waterTank.getFluidAmount() < 1000) ret = waterTank.fill(new FluidStack(FluidRegistry.WATER, 1000), true) > 0;
                else if(item.isItemEqual(Items.LAVA_BUCKET.getDefaultInstance())) if(lavaTank.getFluidAmount()  < 1000) ret = lavaTank .fill(new FluidStack(FluidRegistry.LAVA , 1000), true) > 0;
                else if(item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)){
                    IFluidHandlerItem handler = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
                    if(handler != null) for(IFluidTankProperties prop : handler.getTankProperties()) {
                        FluidStack fluid = prop.getContents();
                        if(fluid == null) continue;
                        else if (fluid.isFluidEqual(new FluidStack(FluidRegistry.LAVA , 0))) ret = lavaTank .fill(handler.drain(new FluidStack(FluidRegistry.LAVA , lavaTank .getCapacity() - lavaTank .getFluidAmount()), true), true) > 0;
                        else if (fluid.isFluidEqual(new FluidStack(FluidRegistry.WATER, 0))) ret = waterTank.fill(handler.drain(new FluidStack(FluidRegistry.WATER, waterTank.getCapacity() - waterTank.getFluidAmount()), true), true) > 0;
                        if(lavaTank.getCapacity() == lavaTank.getFluidAmount() && waterTank.getCapacity() == waterTank.getFluidAmount()) break;
                    }
                }
            }
	    }
	    return ret;
    }

    @Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) { return state.withProperty(FACING, ((TileEntityLavaStation)worldIn.getTileEntity(pos)).getFacing()); }

	@Override
	public Class<? extends TileEntity> getTileEntityClass() { return TileEntityLavaStation.class; }

}
