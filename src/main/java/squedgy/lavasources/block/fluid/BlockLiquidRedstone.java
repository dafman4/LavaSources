package squedgy.lavasources.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import squedgy.lavasources.LavaSources;
import squedgy.lavasources.init.ModFluids;

public class BlockLiquidRedstone extends BlockFluidClassic {

    public BlockLiquidRedstone(){
        super(ModFluids.LIQUID_REDSTONE, Material.WATER);
        this.setCreativeTab(LavaSources.CREATIVE_TAB);
        this.setUnlocalizedName("liquid_redstone");
        this.setRegistryName(LavaSources.MOD_ID, "liquid_redstone");
        ModFluids.LIQUID_REDSTONE.setBlock(this);
    }

//<editor-fold defaultstate="collapsed" desc=". . . . Fields/Constructors">


//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=". . . . Getters/Setters">


//</editor-fold>
}
