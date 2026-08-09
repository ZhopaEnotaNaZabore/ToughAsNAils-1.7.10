package toughasnails.temperature.modifier;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import toughasnails.api.temperature.IModifierMonitor;
import toughasnails.api.temperature.Temperature;
import toughasnails.init.ModConfig;
import toughasnails.util.BiomeUtils;
import toughasnails.util.TerrainUtils;

public class BiomeModifier extends TemperatureModifier
{
    public BiomeModifier(String id)
    {
        super(id);
    }

    @Override
    public Temperature applyEnvironmentModifiers(World world, int x, int y, int z, Temperature initialTemperature, IModifierMonitor monitor)
    {
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        BiomeGenBase biomeNorth = world.getBiomeGenForCoords(x, z - 10);
        BiomeGenBase biomeSouth = world.getBiomeGenForCoords(x, z + 10);
        BiomeGenBase biomeEast = world.getBiomeGenForCoords(x + 10, z);
        BiomeGenBase biomeWest = world.getBiomeGenForCoords(x - 10, z);
        
        float biomeTemp = ((BiomeUtils.getBiomeTempNorm(biome) + BiomeUtils.getBiomeTempNorm(biomeNorth) + BiomeUtils.getBiomeTempNorm(biomeSouth) + BiomeUtils.getBiomeTempNorm(biomeEast) + BiomeUtils.getBiomeTempNorm(biomeWest)) / 5.0F);
        
        //Denormalize, multiply by the max temp offset, add to the current temp
        int newTemperatureLevel = initialTemperature.getRawValue();
        
        int temperatureModifier = (int)Math.round((biomeTemp * 2.0F - 1.0F) * ModConfig.temperature.maxBiomeTempOffset);
        
        // Apply underground coefficient
        if (world.provider.isSurfaceWorld() && ModConfig.temperature.enableUndergroundEffect)
            temperatureModifier = Math.round(TerrainUtils.getAverageUndergroundCoefficient(world, x, y, z) * temperatureModifier);
        newTemperatureLevel += temperatureModifier;
        
        monitor.addEntry(new IModifierMonitor.Context(this.getId(), "Biome Temperature", initialTemperature, new Temperature(newTemperatureLevel)));
        
        return new Temperature(newTemperatureLevel);
    }

    @Override
    public boolean isPlayerSpecific()
    {
        return false;
    }
}
