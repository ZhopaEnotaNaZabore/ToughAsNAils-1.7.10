package toughasnails.util;

import net.minecraft.world.World;
import toughasnails.init.ModConfig;

public class TerrainUtils
{
    public static boolean isUnderground(World world, int x, int y, int z)
    {
        if (y >= world.getHeightValue(x, z))
            return false;

        int width = 5;
        for (int checkX = x - width; checkX < x + width; ++checkX)
            if (world.canBlockSeeTheSky(checkX, y, z))
                return false;
        for (int checkZ = z - width; checkZ < z + width; ++checkZ)
            if (world.canBlockSeeTheSky(x, y, checkZ))
                return false;
        return true;
    }

    public static float getUndergroundCoefficient(World world, int x, int y, int z)
    {
        if (!isUnderground(world, x, y, z))
            return 1.0F;

        int depth = world.getHeightValue(x, z) - y;
        int equilibriumDepth = ModConfig.temperature.equilibriumDepth;
        return depth >= equilibriumDepth ? 0.0F : 1.0F - (float)depth / equilibriumDepth;
    }

    public static float getAverageUndergroundCoefficient(World world, int x, int y, int z)
    {
        float total = 0.0F;
        int width = 3;
        int divider = 2;
        int count = 0;
        for (int checkX = x - width; checkX <= x + width; ++checkX)
            for (int checkY = y; checkY <= y + 2 * width; ++checkY)
                for (int checkZ = z - width; checkZ <= z + width; ++checkZ)
                {
                    if (count % divider == 0)
                        total += getUndergroundCoefficient(world, checkX, y, checkZ);
                    ++count;
                }
        return total / ((int)Math.pow(2 * width + 1, 3) / divider + 1.0F);
    }
}
