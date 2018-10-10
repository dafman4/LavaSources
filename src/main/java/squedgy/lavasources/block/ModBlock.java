package squedgy.lavasources.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.generic.IModCraftable;
import squedgy.lavasources.generic.IPotentialOreDictionaryMember;
import squedgy.lavasources.init.ModBlocks;
import squedgy.lavasources.research.Research;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ModBlock extends BlockContainer implements IPotentialOreDictionaryMember, IModCraftable {
	protected final int BLOCK_ID;
	private final List<Research> required = new ArrayList<>();

	public ModBlock(String unlocalizedName, int BLOCK_ID, float hardness, float resistance, SoundType sound){
		super(Material.IRON);
		this.BLOCK_ID = BLOCK_ID;
		this.setUnlocalizedName(unlocalizedName);
		this.setRegistryName(LavaSources.MOD_ID, unlocalizedName);
		this.setCreativeTab(LavaSources.CREATIVE_TAB);
		this.setSoundType(sound);
		this.setHardness(hardness);
		this.setResistance(resistance);
	}
	
	public ModBlock(String unlocalizedName, int BLOCK_ID){
		this(unlocalizedName, BLOCK_ID, 4, 20, SoundType.METAL);
	}

	@Override
	public abstract boolean hasTileEntity();

	@Override
	public abstract TileEntity createNewTileEntity(World worldIn, int meta);

	public abstract IProperty[] getProperties();
	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, getProperties());
	}

	public boolean isPersistent(){return false;}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if(this.isPersistent()){

		}else{
			super.getDrops(drops,world,pos,state,fortune);
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Item.getItemFromBlock(ModBlocks.BLOCKS.get(BLOCK_ID));
	}
	@Override
	public abstract void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack);
	@Override
	public abstract int getMetaFromState(IBlockState state);
	@Override
	public abstract IBlockState getStateFromMeta(int meta);
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public abstract IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos);

	public abstract Class<? extends TileEntity> getTileEntityClass();

    @Override
    public boolean hasOreDictionaryName() { return false; }

    @Override
    public String getOreDictionaryName() { return null; }

    public static boolean isStateEqualTo(IBlockState state, IBlockState state2){
    	if(state.getBlock() instanceof  ModBlock && state2.getBlock() instanceof ModBlock){
    		Block block1 = state.getBlock(), block2 = state2.getBlock();
    		if(block1.getClass().equals(block2.getClass())){
    			for(IProperty p : ((ModBlock) block1).getProperties()) if(state.getValue(p) != state2.getValue(p))return false;
                return true;
		    }
	    }
	    return false;
    }

	@Override
	public final List<Research> getRequirements() {
		return new ArrayList<>(required);
	}

	@Override
	public final void addRequirement(Research requirement) {
		if(this.required.indexOf(requirement) < 0) required.add(requirement);
	}

}
