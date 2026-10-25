package toughasnails.handler.thirst;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import toughasnails.api.TANPotions;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.ItemDrink;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.thirst.ThirstHelper;
import toughasnails.api.thirst.WaterType;
import toughasnails.config.json.DrinkData;
import toughasnails.fluids.blocks.BlockPurifiedWaterFluid;
import toughasnails.handler.PacketHandler;
import toughasnails.init.ModConfig;
import toughasnails.network.message.MessageDrinkWaterInWorld;
import toughasnails.thirst.ThirstHandler;

public class DrinkHandler
{
    /** Untreated source water can make the player sick. */
    private static final float POND_POISON_CHANCE = 0.30F;

    @SubscribeEvent
    public void onItemUseFinish(PlayerUseItemEvent.Finish event)
    {
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = event.item;
        ThirstHandler thirst = (ThirstHandler) ThirstHelper.getThirstData(player);
        if (stack == null || !thirst.isThirsty()) return;

        if (stack.getItem() == Items.potionitem)
        {
            List effects = ((ItemPotion) stack.getItem()).getEffects(stack);
            if (effects == null || effects.isEmpty())
            {
                applyDrink(player, WaterType.NORMAL.getThirst(), WaterType.NORMAL.getHydration(), WaterType.NORMAL.getPoisonChance());
            }
            else thirst.addStats(4, 0.3F);
        }
        else if (!(stack.getItem() instanceof ItemDrink))
        {
            String name = (String) Item.itemRegistry.getNameForObject(stack.getItem());
            if (name != null && ModConfig.drinkData.containsKey(name))
            {
                for (DrinkData data : ModConfig.drinkData.get(name))
                {
                    if (data.getPredicate().apply(stack))
                    {
                        applyDrinkFromData(player, data);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if ((event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
                && event.entityPlayer.isSneaking() && event.entityPlayer.getCurrentEquippedItem() == null
                && (SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_WORLD)
                        || SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_RAIN))
                && ThirstHelper.getThirstData(event.entityPlayer).getThirst() < 20 && event.world.isRemote)
        {
            tryDrinkWaterInWorld(event.entityPlayer, true);
        }
    }

    public static void tryDrinkWaterInWorld(EntityPlayer player, boolean client)
    {
        if (!player.isEntityAlive() || !player.isSneaking() || player.getCurrentEquippedItem() != null
                || !SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST)
                || ThirstHelper.getThirstData(player).getThirst() >= 20) return;
        TargetWater target = getRightClickedWater(player);
        if (target == null) return;

        if (client)
        {
            PacketHandler.instance.sendToServer(new MessageDrinkWaterInWorld());
            player.playSound("random.drink", 0.5F, 1.0F);
            player.swingItem();
        }
        else
        {
            applyDrinkFromWaterType(player, target.type);
            if (target.type == WaterType.NORMAL && player.worldObj.rand.nextFloat() < POND_POISON_CHANCE)
            {
                player.addPotionEffect(new PotionEffect(Potion.poison.id, 600, 0));
            }
            if (target.type == WaterType.PURIFIED) player.worldObj.setBlockToAir(target.x, target.y, target.z);
        }
    }

    private static TargetWater getRightClickedWater(EntityPlayer player)
    {
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        if (SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_RAIN) && player.worldObj.isRaining()
                && player.rotationPitch < -75.0F && player.worldObj.canBlockSeeTheSky(x, y, z))
        {
            return new TargetWater(0, 0, 0, WaterType.RAIN);
        }
        if (!SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST_WORLD)) return null;

        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1.0F);
        MovingObjectPosition hit = player.worldObj.rayTraceBlocks(origin,
                origin.addVector(look.xCoord * 5.0D, look.yCoord * 5.0D, look.zCoord * 5.0D), true);
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            Block block = player.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            if (block instanceof BlockPurifiedWaterFluid) return new TargetWater(hit.blockX, hit.blockY, hit.blockZ, WaterType.PURIFIED);
            if (block == Blocks.water || block == Blocks.flowing_water) return new TargetWater(hit.blockX, hit.blockY, hit.blockZ, WaterType.NORMAL);
        }
        return null;
    }

    private static void applyDrinkFromData(EntityPlayer player, DrinkData data)
    {
        applyDrink(player, data.getThirstRestored(), data.getHydrationRestored(), data.getPoisonChance());
    }

    private static void applyDrinkFromWaterType(EntityPlayer player, WaterType waterType)
    {
        // The pond penalty is vanilla Poison (30%) below; do not also apply
        // TAN's bottled-water thirst penalty for the same drink.
        float poisonChance = waterType == WaterType.NORMAL ? 0.0F : waterType.getPoisonChance();
        applyDrink(player, waterType.getThirst(), waterType.getHydration(), poisonChance);
    }

    private static void applyDrink(EntityPlayer player, int thirst, float hydration, float poisonChance)
    {
        IThirst stats = ThirstHelper.getThirstData(player);
        stats.addStats(thirst, hydration);
        if (!player.worldObj.isRemote && player.worldObj.rand.nextFloat() < poisonChance
                && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
        {
            player.addPotionEffect(new PotionEffect(TANPotions.thirst.id, 600));
        }
    }

    private static class TargetWater
    {
        final int x, y, z;
        final WaterType type;
        TargetWater(int x, int y, int z, WaterType type) { this.x = x; this.y = y; this.z = z; this.type = type; }
    }
}
