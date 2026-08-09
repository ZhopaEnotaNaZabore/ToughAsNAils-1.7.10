package toughasnails.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import toughasnails.api.ITANBlock;

/** ItemBlock variant selector backed by the 1.7.10 metadata value. */
public class ItemTANBlock extends ItemBlock
{
    private final ITANBlock tanBlock;

    public ItemTANBlock(Block block)
    {
        super(block);
        if (!(block instanceof ITANBlock))
            throw new IllegalArgumentException("ItemTANBlock requires an ITANBlock");
        tanBlock = (ITANBlock) block;
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List subItems)
    {
        if (getCreativeTab() != tab)
            return;

        int[] presets = tanBlock.getPresetMetadatas();
        if (presets == null || presets.length == 0)
            subItems.add(new ItemStack(this, 1, 0));
        else
            for (int metadata : presets)
                subItems.add(new ItemStack(this, 1, metadata));
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + "." + tanBlock.getStateName(stack.getItemDamage());
    }
}
