package toughasnails.handler.thirst;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import toughasnails.api.TANCapabilities;
import toughasnails.thirst.ThirstHandler;

public class ThirstStatHandler
{
    @SubscribeEvent
    public void onPlayerJump(LivingJumpEvent event)
    {
        World world = event.entity.worldObj;

        if (!world.isRemote && event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (!player.capabilities.isCreativeMode) {
                ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.THIRST);

                if (player.isSprinting()) {
                    thirstStats.addExhaustion(0.8F);
                } else {
                    thirstStats.addExhaustion(0.2F);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event)
    {
        World world = event.entity.worldObj;

        if (!world.isRemote && event.ammount != 0.0F)
        {
            if (event.entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)event.entity;

                if (!player.capabilities.isCreativeMode)
                {
                    ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.THIRST);
                    //Uses hunger values for now, may change in the future
                    thirstStats.addExhaustion(event.source.getHungerDamage());
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerAttackEntity(AttackEntityEvent event)
    {
        World world = event.entityPlayer.worldObj;
        if (!world.isRemote && !event.entityPlayer.capabilities.isCreativeMode)
        {
            ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(event.entityPlayer, TANCapabilities.THIRST);
            thirstStats.addExhaustion(0.3F);
        }
    }
    
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        World world = event.world;
        EntityPlayer player = event.getPlayer();
        
        if (!world.isRemote && !player.capabilities.isCreativeMode)
        {
            boolean canHarvestBlock = event.block.canHarvestBlock(player, event.blockMetadata);
            
            if (canHarvestBlock)
            {
                //The only part of this method that is new - the rest is recreating the surrounding circumstances in func_180237_b
                ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.THIRST);
                
                thirstStats.addExhaustion(0.025F);
            }
        }
    }
}
