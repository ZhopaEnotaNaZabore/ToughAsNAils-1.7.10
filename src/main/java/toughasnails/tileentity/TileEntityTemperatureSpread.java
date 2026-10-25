package toughasnails.tileentity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.PlayerStatRegistry;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.temperature.ITemperatureRegulator;
import toughasnails.api.temperature.Temperature;

/**
 * Legacy implementation of the coil's enclosed-air flood fill. Coordinates are
 * stored as ChunkCoordinates because 1.7.10 has no BlockPos.
 */
public class TileEntityTemperatureSpread extends TileEntity implements ITemperatureRegulator
{
    public static final int MAX_SPREAD_DISTANCE = 50;
    public static final int RATE_MODIFIER = -500;
    public static final boolean ENABLE_DEBUG = false;

    private Set<ChunkCoordinates>[] filledPositions;
    private Set<ChunkCoordinates> obstructedPositions;
    private int updateTicks;
    private int temperatureModifier;
    private AxisAlignedBB maxSpreadBox;

    public TileEntityTemperatureSpread()
    {
        filledPositions = new Set[MAX_SPREAD_DISTANCE + 1];
        for (int i = 0; i <= MAX_SPREAD_DISTANCE; ++i)
            filledPositions[i] = new HashSet<ChunkCoordinates>();
        obstructedPositions = new HashSet<ChunkCoordinates>();
    }

    public TileEntityTemperatureSpread(int temperatureModifier)
    {
        this();
        this.temperatureModifier = temperatureModifier;
    }

    @Override
    public void updateEntity()
    {
        if (worldObj.isRemote) return;
        if ((worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 8) == 0)
        {
            reset();
            return;
        }

        if (++updateTicks % 20 != 0)
            return;

        if (!isActive() || !verify())
            fill();

        if (maxSpreadBox == null)
            maxSpreadBox = AxisAlignedBB.getBoundingBox(xCoord - MAX_SPREAD_DISTANCE, yCoord - MAX_SPREAD_DISTANCE, zCoord - MAX_SPREAD_DISTANCE, xCoord + MAX_SPREAD_DISTANCE, yCoord + MAX_SPREAD_DISTANCE, zCoord + MAX_SPREAD_DISTANCE);

        for (Object object : worldObj.getEntitiesWithinAABB(EntityPlayer.class, maxSpreadBox))
        {
            EntityPlayer player = (EntityPlayer)object;
            int distance = Math.abs((int)player.posX - xCoord) + Math.abs((int)player.posY - yCoord) + Math.abs((int)player.posZ - zCoord);
            boolean collided = false;
            for (int strength = MAX_SPREAD_DISTANCE - distance; strength >= 0 && !collided; --strength)
            {
                for (ChunkCoordinates position : filledPositions[strength])
                {
                    AxisAlignedBB box = AxisAlignedBB.getBoundingBox(position.posX, position.posY, position.posZ, position.posX + 1.0D, position.posY + 1.0D, position.posZ + 1.0D);
                    if (player.boundingBox.intersectsWith(box))
                    {
                        collided = true;
                        break;
                    }
                }
            }
            if (collided)
            {
                ITemperature temperature = (ITemperature)PlayerStatRegistry.getStat(player, TANCapabilities.TEMPERATURE);
                if (temperature != null)
                    temperature.applyModifier("Climatisation", temperatureModifier, RATE_MODIFIER, 60);
            }
        }
    }

    public void reset()
    {
        for (Set<ChunkCoordinates> positions : filledPositions)
            positions.clear();
        obstructedPositions.clear();
    }

    public void fill()
    {
        reset();
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            ChunkCoordinates position = offset(xCoord, yCoord, zCoord, direction);
            if (canFill(position))
                filledPositions[MAX_SPREAD_DISTANCE].add(position);
        }
        runStage(MAX_SPREAD_DISTANCE - 1);
    }

    private void runStage(int strength)
    {
        if (strength <= 0)
            return;
        for (ChunkCoordinates position : filledPositions[strength + 1])
            spreadAroundPos(position, strength);
        runStage(strength - 1);
    }

    private void spreadAroundPos(ChunkCoordinates position, int strength)
    {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            ChunkCoordinates next = offset(position.posX, position.posY, position.posZ, direction);
            if (!filledPositions[strength + 1].contains(next))
                setTrackedStrength(next, strength);
        }
    }

    private void setTrackedStrength(ChunkCoordinates position, int strength)
    {
        if (canFill(position))
            filledPositions[strength].add(position);
        else
            obstructedPositions.add(position);
    }

    public boolean verify()
    {
        for (Set<ChunkCoordinates> positions : filledPositions)
            for (ChunkCoordinates position : positions)
                if (!canFill(position))
                    return false;
        for (ChunkCoordinates position : obstructedPositions)
            if (canFill(position))
                return false;
        return true;
    }

    public boolean isActive()
    {
        return !filledPositions[MAX_SPREAD_DISTANCE].isEmpty();
    }

    private boolean canFill(ChunkCoordinates position)
    {
        return !worldObj.getBlock(position.posX, position.posY, position.posZ).isOpaqueCube() && !worldObj.canBlockSeeTheSky(position.posX, position.posY, position.posZ);
    }

    private static ChunkCoordinates offset(int x, int y, int z, ForgeDirection direction)
    {
        return new ChunkCoordinates(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        temperatureModifier = compound.getInteger("TemperatureModifier");
        for (int i = 0; i <= MAX_SPREAD_DISTANCE; ++i)
            filledPositions[i] = readPositions(compound.getCompoundTag("Strength" + i));
        obstructedPositions = readPositions(compound.getCompoundTag("ObstructedPositions"));
        maxSpreadBox = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("TemperatureModifier", temperatureModifier);
        for (int i = 0; i <= MAX_SPREAD_DISTANCE; ++i)
            compound.setTag("Strength" + i, writePositions(filledPositions[i]));
        compound.setTag("ObstructedPositions", writePositions(obstructedPositions));
    }

    private static NBTTagCompound writePositions(Set<ChunkCoordinates> positions)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Count", positions.size());
        int index = 0;
        for (ChunkCoordinates position : positions)
        {
            NBTTagCompound value = new NBTTagCompound();
            value.setInteger("X", position.posX);
            value.setInteger("Y", position.posY);
            value.setInteger("Z", position.posZ);
            compound.setTag("Pos" + index++, value);
        }
        return compound;
    }

    private static Set<ChunkCoordinates> readPositions(NBTTagCompound compound)
    {
        Set<ChunkCoordinates> positions = new HashSet<ChunkCoordinates>();
        for (int i = 0; i < compound.getInteger("Count"); ++i)
        {
            NBTTagCompound value = compound.getCompoundTag("Pos" + i);
            positions.add(new ChunkCoordinates(value.getInteger("X"), value.getInteger("Y"), value.getInteger("Z")));
        }
        return positions;
    }

    @Override
    public Temperature getRegulatedTemperature()
    {
        return new Temperature(temperatureModifier);
    }

    @Override
    public boolean isPosRegulated(int x, int y, int z)
    {
        ChunkCoordinates position = new ChunkCoordinates(x, y, z);
        for (int i = 0; i < MAX_SPREAD_DISTANCE; ++i)
            if (filledPositions[i].contains(position))
                return true;
        return false;
    }
}
