package squedgy.lavasources.generic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockShulkerBox;


public abstract class ModPersistentBlock extends ModBlock{

	public ModPersistentBlock(String unlocalizedName, int BLOCK_ID) {
		super(unlocalizedName, BLOCK_ID);
	}

	public boolean isPersistent(){return true;}

	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state)
	{
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof IPersistentInventory){
			IPersistentInventory modifier = (IPersistentInventory) te;
			if(!modifier.isDestroyedByCreative()) {
				ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
				if (modifier.shouldDropSpecial()) {
					NBTTagCompound compound1 = new NBTTagCompound();
					NBTTagCompound compound2 = new NBTTagCompound();
					compound1.setTag("BlockEntityTag", modifier.writeItem(compound2));
					stack.setTagCompound(compound1);
				}
				spawnAsEntity(worldIn, pos, stack);
			}
			worldIn.updateComparatorOutputLevel(pos, state.getBlock());
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state){
		ItemStack stack = super.getItem(worldIn, pos, state);
		IPersistentInventory modifier = (IPersistentInventory) worldIn.getTileEntity(pos);
		NBTTagCompound compound = modifier.writeItem(new NBTTagCompound());
		if(!compound.hasNoTags()){
			stack.setTagInfo("BlockEntityTag", compound);
		}
		return stack;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(worldIn.getTileEntity(pos) instanceof IPersistentInventory){
			IPersistentInventory pi = (IPersistentInventory) worldIn.getTileEntity(pos);
			pi.setDestroyedByCreative(player.isCreative());
		}
	}
}
