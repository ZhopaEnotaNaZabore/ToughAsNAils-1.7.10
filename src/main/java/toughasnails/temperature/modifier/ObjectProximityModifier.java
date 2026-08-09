package toughasnails.temperature.modifier;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import toughasnails.api.temperature.IModifierMonitor;
import toughasnails.api.temperature.Temperature;
import toughasnails.config.json.BlockTemperatureData;
import toughasnails.init.ModConfig;

import java.util.List;

//TODO: Replace this with something better
public class ObjectProximityModifier extends TemperatureModifier
{
    public ObjectProximityModifier(String id)
    {
        super(id);
    }

    @Override
    public Temperature applyEnvironmentModifiers(World world, int baseX, int baseY, int baseZ, Temperature initialTemperature, IModifierMonitor monitor)
    {
        int temperatureLevel = initialTemperature.getRawValue();
        int newTemperatureLevel = temperatureLevel;

        float blockTemperatureModifier = 0.0F;

        for (int x = -3; x <= 3; x++)
        {
            for (int y = -2; y <= 2; y++)
            {
                for (int z = -3; z <= 3; z++)
                {
                    int x2 = baseX + x;
                    int y2 = baseY + y - 1;
                    int z2 = baseZ + z;
                    float mod = getBlockTemperature(world, x2, y2, z2);

                    // use the most drastic temperature affecting block's temperature
                    if (Math.abs(mod) > Math.abs(blockTemperatureModifier))
                    {
                        blockTemperatureModifier = mod;
                    }
                }
            }
        }

        newTemperatureLevel += blockTemperatureModifier;
        monitor.addEntry(new IModifierMonitor.Context(this.getId(), "Nearby Blocks", initialTemperature, new Temperature(newTemperatureLevel)));

        return new Temperature(newTemperatureLevel);
    }

    public static float getBlockTemperature(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        Material material = block.getMaterial();
        Object registryName = Block.blockRegistry.getNameForObject(block);
        if (registryName == null) return 0.0F;
        String blockName = registryName.toString();

        //Blocks
        if (ModConfig.blockTemperatureData.containsKey(blockName))
        {
            List<BlockTemperatureData> blockTempData = ModConfig.blockTemperatureData.get(blockName);

            //Check if block has relevant state: 
            for (BlockTemperatureData tempData : blockTempData)
            {
                if (tempData.predicate.matches(block, world.getBlockMetadata(x, y, z)))
                {
                    return tempData.blockTemperature;
                }
            }

            // If no matching states, then block is at ambient temperature:
            return 0.0F;
        }

        //Handle materials, but only if we didn't already find an actual block to use: 
        if (material == Material.fire)
        {
            return ModConfig.materialTemperatureData.fire;
        }

        return 0.0F;
    }

    @Override
    public boolean isPlayerSpecific()
    {
        return false;
    }
}
