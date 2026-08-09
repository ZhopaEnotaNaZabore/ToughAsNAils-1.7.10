package toughasnails.init;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fluids.FluidRegistry;
import toughasnails.api.ITANBlock;
import toughasnails.api.TANBlocks;
import toughasnails.block.BlockRainCollector;
import toughasnails.block.BlockTANCampfire;
import toughasnails.block.BlockTANTemperatureCoil;
import toughasnails.core.ToughAsNails;
import toughasnails.fluids.PurifiedWaterFluid;
import toughasnails.fluids.blocks.BlockPurifiedWaterFluid;
import toughasnails.tileentity.TileEntityTemperatureSpread;
import toughasnails.util.inventory.CreativeTabTAN;

public class ModBlocks
{
    public static void init()
    {
        TANBlocks.campfire = registerBlock(new BlockTANCampfire().setBlockTextureName("toughasnails:log_burnt"), "campfire");
        TANBlocks.rain_collector = registerBlock(new BlockRainCollector().setBlockTextureName("toughasnails:rain_collector_side"), "rain_collector");
        TANBlocks.temperature_coil = registerBlock(new BlockTANTemperatureCoil().setBlockTextureName("toughasnails:cooling_coil_off"), "temperature_coil");

        TANBlocks.purified_water_fluid = PurifiedWaterFluid.instance;
        FluidRegistry.registerFluid(TANBlocks.purified_water_fluid);
        TANBlocks.purified_water = new BlockPurifiedWaterFluid(TANBlocks.purified_water_fluid).setBlockName("purified_water");
        GameRegistry.registerBlock(TANBlocks.purified_water, "purified_water");
        TANBlocks.purified_water_fluid.setBlock(TANBlocks.purified_water);

        GameRegistry.registerTileEntity(TileEntityTemperatureSpread.class, "toughasnails_temperature_spread");
    }

    private static Block registerBlock(Block block, String name)
    {
        block.setBlockName(name);
        block.setCreativeTab(CreativeTabTAN.instance);
        Class<? extends ItemBlock> itemClass = block instanceof ITANBlock ? ((ITANBlock)block).getItemClass() : ItemBlock.class;
        GameRegistry.registerBlock(block, itemClass, name);
        return block;
    }
}
