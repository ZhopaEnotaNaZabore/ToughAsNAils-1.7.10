package toughasnails.handler;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import toughasnails.api.TANBlocks;
import toughasnails.api.item.TANItems;

/** Supplies the dedicated purified-water bucket used by Forge 1.7.10. */
public class BucketEventHandler
{
    @SubscribeEvent
    public void onRightClickHoldingBucket(FillBucketEvent event)
    {
        if (event.current == null || event.current.getItem() != Items.bucket || event.target == null
                || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
        {
            return;
        }

        int x = event.target.blockX;
        int y = event.target.blockY;
        int z = event.target.blockZ;
        if (event.world.getBlock(x, y, z) == TANBlocks.purified_water && event.world.getBlockMetadata(x, y, z) == 0)
        {
            event.result = new ItemStack(TANItems.purified_water_bucket);
            event.setResult(Result.ALLOW);
            event.world.setBlockToAir(x, y, z);
        }
    }
}
