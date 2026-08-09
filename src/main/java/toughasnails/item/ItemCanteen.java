package toughasnails.item;

import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import toughasnails.api.config.SyncedConfig;
import toughasnails.api.item.ItemDrink;
import toughasnails.api.TANCapabilities;
import toughasnails.api.TANPotions;
import toughasnails.api.thirst.WaterType;
import toughasnails.api.config.GameplayOption;
import toughasnails.thirst.ThirstHandler;

public class ItemCanteen extends ItemDrink<WaterType>
{
    private IIcon emptyIcon;
    private IIcon filledIcon;
    public ItemCanteen()
    {
        this.setMaxDamage(3);
        this.setNoRepair();
    }
    
    @Override
    public WaterType getTypeFromMeta(int meta)
    {
        int type = meta & 3;
        
        return type > 0 ? WaterType.values()[type - 1] : null;
    }
    
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer entity)
    {
        WaterType waterType = this.getTypeFromMeta(stack.getItemDamage());
        
        if (entity instanceof EntityPlayer && waterType != null)
        {
            EntityPlayer player = (EntityPlayer)entity;

            if (!player.capabilities.isCreativeMode)
            {
                int damage = (stack.getItemDamage() >> 2) + 1;
                int typeIndex = (waterType.ordinal() + 1);
                
                //Reset the canteen to its empty state
                if (damage == this.getMaxDamage())
                {
                    damage = 0;
                    typeIndex = 0;
                }
                
                this.setDamage(stack, typeIndex | (damage << 2));
            }

            ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.THIRST);
            thirstStats.addStats(waterType.getThirst(), waterType.getHydration());
            
            if (!world.isRemote && world.rand.nextFloat() < waterType.getPoisonChance() && SyncedConfig.getBooleanValue(GameplayOption.ENABLE_THIRST))
            {
                player.addPotionEffect(new PotionEffect(TANPotions.thirst.id, 600));
            }
        }

        return stack;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        ThirstHandler thirstStats = (ThirstHandler)toughasnails.api.stat.PlayerStatRegistry.getStat(player, TANCapabilities.THIRST);
        WaterType waterType = this.getTypeFromMeta(stack.getItemDamage());
        
        if (!attemptCanteenFill(player, stack) && waterType != null && getTimesUsed(stack) < 3 && thirstStats.isThirsty())
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        
        return stack;
    }
    
    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return (stack.getItemDamage() >> 2) / (double)stack.getMaxDamage();
    }
    
    /**
     * Attempt to fill the provided canteen stack with water.
     * @param player The player holding the canteen.
     * @param stack The canteen item stack.
     * @return true if successful, otherwise false.
     */
    private boolean attemptCanteenFill(EntityPlayer player, ItemStack stack)
    {
        World world = player.worldObj;
        MovingObjectPosition movingObjectPos = this.getMovingObjectPositionFromPlayer(world, player, true);
        
        if (movingObjectPos != null && movingObjectPos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int x = movingObjectPos.blockX;
            int y = movingObjectPos.blockY;
            int z = movingObjectPos.blockZ;
            net.minecraft.block.Block block = world.getBlock(x, y, z);
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

            if (fluid != null && fluid == FluidRegistry.WATER) //Temporary, until a registry is created
            {
                stack.setItemDamage(1);
                return true;
            }
            else if (block instanceof BlockCauldron)
            {
                BlockCauldron cauldron = (BlockCauldron)block;
                int level = BlockCauldron.func_150027_b(world.getBlockMetadata(x, y, z));
                
                if (level > 0 && !world.isRemote)
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        stack.setItemDamage(1);
                    }

                    cauldron.func_150024_a(world, x, y, z, level - 1);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private int getTimesUsed(ItemStack stack)
    {
        return stack.getItemDamage() >> 2;
    }

    @Override
    public void registerIcons(IIconRegister register)
    {
        emptyIcon = register.registerIcon("toughasnails:canteen_empty");
        filledIcon = register.registerIcon("toughasnails:canteen_filled");
        itemIcon = emptyIcon;
    }

    @Override
    public IIcon getIconFromDamage(int damage)
    {
        return (damage & 3) == 0 ? emptyIcon : filledIcon;
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
		WaterType type = this.getTypeFromMeta(stack.getItemDamage());
		        
        if (type != null)
        {
        	String name = type.getDescription();
        	return name + " " + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
        }
        
        return StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }
}
