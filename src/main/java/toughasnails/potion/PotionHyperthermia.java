package toughasnails.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import toughasnails.api.config.GameplayOption;
import toughasnails.api.config.SyncedConfig;

public class PotionHyperthermia extends TANPotion
{
    public PotionHyperthermia(int id)
    {
        super(id, true, 0xFFA300, 3, 0);
    
        this.setIconIndex(3, 0);
    }
    
    @Override
    public void performEffect(EntityLivingBase entity, int amplifier)
    {
    	EnumDifficulty enumdifficulty = entity.worldObj.difficultySetting;
    	if (!(entity instanceof EntityPlayer) || (enumdifficulty == EnumDifficulty.PEACEFUL && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_PEACEFUL) && entity.getHealth() > 10.0F) ||  (enumdifficulty == EnumDifficulty.EASY && entity.getHealth() > 10.0F) || (enumdifficulty == EnumDifficulty.NORMAL && entity.getHealth() > 1.0F) || enumdifficulty == EnumDifficulty.HARD)
    	{
    		entity.attackEntityFrom(DamageSource.generic, 0.5F);
    	}
    }
    
    @Override
    public boolean isReady(int duration, int amplifier)
    {
        int time = 50 >> amplifier;
        return time > 0 ? duration % time == 0 : true;
    }
}
