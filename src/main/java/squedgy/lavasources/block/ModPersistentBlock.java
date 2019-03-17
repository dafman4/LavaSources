package squedgy.lavasources.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squedgy.lavasources.generic.tileentities.IPersistentInventory;


public abstract class ModPersistentBlock extends ModBlock {

	public ModPersistentBlock(String unlocalizedName, int BLOCK_ID) {
		super(unlocalizedName, BLOCK_ID);
	}

	@Override
	public final boolean hasTileEntity() { return true; }

	public final boolean isPersistent(){return true;}

	@SideOnly(Side.CLIENT)
	public final boolean hasCustomBreakingProgress(IBlockState state)
	{
		return true;
	}

	@Override
	public final void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof IPersistentInventory){
			IPersistentInventory modifier = (IPersistentInventory) te;
			if(!modifier.isDestroyedByCreative()) {
				ItemStack stack = getItem(worldIn, pos, state);
				spawnAsEntity(worldIn, pos, stack);
			}
			worldIn.updateComparatorOutputLevel(pos, state.getBlock());
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public final ItemStack getItem(World worldIn, BlockPos pos, IBlockState state){
		ItemStack stack = super.getItem(worldIn, pos, state);
		IPersistentInventory modifier = (IPersistentInventory) worldIn.getTileEntity(pos);
		if(!modifier.isDestroyedByCreative()) stack.setTagInfo("BlockEntityTag", modifier.writeItem(new NBTTagCompound()));
		return stack;
	}

	@Override
	public final void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(worldIn.getTileEntity(pos) instanceof IPersistentInventory){
			IPersistentInventory pi = (IPersistentInventory) worldIn.getTileEntity(pos);
			pi.setDestroyedByCreative(player.isCreative());
		}
	}
}
