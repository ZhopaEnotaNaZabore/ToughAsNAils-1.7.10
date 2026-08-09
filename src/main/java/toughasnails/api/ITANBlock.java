package toughasnails.api;

import net.minecraft.item.ItemBlock;

public interface ITANBlock {
    
    public Class<? extends ItemBlock> getItemClass();
    public int[] getPresetMetadatas();
    public String getStateName(int metadata);
    
}
