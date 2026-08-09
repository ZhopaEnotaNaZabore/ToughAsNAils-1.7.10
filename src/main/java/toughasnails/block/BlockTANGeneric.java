package toughasnails.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import toughasnails.api.ITANBlock;
import toughasnails.item.ItemTANBlock;

public class BlockTANGeneric extends Block implements ITANBlock
{
    
    // implement IBOPBlock
    @Override
    public Class<? extends ItemBlock> getItemClass() { return ItemTANBlock.class; }
    @Override
    public int[] getPresetMetadatas() { return new int[] {0}; }
    @Override
    public String getStateName(int metadata) {return "";}

    
    public BlockTANGeneric() {
        // use rock as default material
        this(Material.rock);
    }
    
    public BlockTANGeneric(Material material)
    {
        super(material);
        // set some defaults
        this.setHardness(1.0F);
        this.setStepSound(soundTypeStone);
    }
    
}
