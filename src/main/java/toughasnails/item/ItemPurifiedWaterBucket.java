package toughasnails.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;

/** Legacy filled bucket for the purified-water fluid. */
public class ItemPurifiedWaterBucket extends ItemBucket
{
    public ItemPurifiedWaterBucket(Block fluidBlock)
    {
        super(fluidBlock);
        setContainerItem(net.minecraft.init.Items.bucket);
    }
}
