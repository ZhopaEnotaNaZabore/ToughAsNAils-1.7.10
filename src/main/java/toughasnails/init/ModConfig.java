/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.init;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import toughasnails.api.TANBlocks;
import toughasnails.config.ConfigHandler;
import toughasnails.config.GameplayConfig;
import toughasnails.config.TemperatureConfig;
import toughasnails.config.json.*;
import toughasnails.util.config.JsonUtil;

import static com.google.common.collect.Lists.newArrayList;
import static toughasnails.api.item.TANItems.*;

public class ModConfig {
    public static List<ConfigHandler> configHandlers = newArrayList();

    public static Map<String, List<BlockTemperatureData>> blockTemperatureData = Maps.newHashMap();
    public static Map<String, List<DrinkData>> drinkData = Maps.newHashMap();
    public static List<ArmorTemperatureData> armorTemperatureData = newArrayList();
    public static MaterialTemperatureData materialTemperatureData;

    public static GameplayConfig gameplay;
    public static TemperatureConfig temperature;

    public static void preInit(File configDir) {
        gameplay = new GameplayConfig(new File(configDir, "gameplay.cfg"));
        temperature = new TemperatureConfig(new File(configDir, "temperature.cfg"));
    }

    public static void init(File configDir) {
        //Block json config:
        List<BlockTemperatureData> defaultBlockTemperatureData = Lists.newArrayList(
                new BlockTemperatureData(new BlockStatePredicate(TANBlocks.campfire, 1, 1), 5.0F),
                new BlockTemperatureData(new BlockStatePredicate(Blocks.lit_furnace), 3.0F),
                new BlockTemperatureData(new BlockStatePredicate(Blocks.lava), 15.0F),
                new BlockTemperatureData(new BlockStatePredicate(Blocks.flowing_lava), 15.0F),
                new BlockTemperatureData(new BlockStatePredicate(Blocks.netherrack), 12.5F));

        List<BlockTemperatureData> blockTempDataList = JsonUtil.getOrCreateConfigFile(configDir, "block_temperature.json", defaultBlockTemperatureData, new TypeToken<List<BlockTemperatureData>>() {
        }.getType());
        populateDataMap(blockTempDataList, blockTemperatureData, tempData -> tempData.predicate.getBlockRegistryName());

        //Material json config:
        materialTemperatureData = JsonUtil.getOrCreateConfigFile(configDir, "material_temperature.json", new MaterialTemperatureData(), new TypeToken<MaterialTemperatureData>() {
        }.getType());

        // Drink json config
        List<DrinkData> defaultDrinkData = newArrayList(
                new DrinkData(new ItemPredicate(Items.milk_bucket, 0), 6, 0.7F, 0.0F));
        List<DrinkData> drinkDataList = JsonUtil.getOrCreateConfigFile(configDir, "drink_stats.json", defaultDrinkData, new TypeToken<List<DrinkData>>() {
        }.getType());
        populateDataMap(drinkDataList, drinkData, drinkData -> registryName(drinkData.getPredicate().getItemStack().getItem()));

        // Armor temperature data
        armorTemperatureData = JsonUtil.getOrCreateConfigFile(configDir, "armor_temp_modifiers.json", newArrayList(
                new ArmorTemperatureData(newArrayList(registryName(jelled_slime_helmet), registryName(jelled_slime_chestplate),
                        registryName(jelled_slime_leggings), registryName(jelled_slime_boots)), -1),
                new ArmorTemperatureData(newArrayList(registryName(wool_helmet), registryName(wool_chestplate),
                        registryName(wool_leggings), registryName(wool_boots)), 1)), new TypeToken<List<ArmorTemperatureData>>() {
        }.getType());
    }

    private static String registryName(Item item) {
        return Item.itemRegistry.getNameForObject(item).toString();
    }

    private static <T> void populateDataMap(List<T> dataList, Map<String, List<T>> dataMap, Function<T, String> keyGetter) {
        for (T data : dataList) {
            String key = keyGetter.apply(data);

            if (!dataMap.containsKey(key)) {
                dataMap.put(key, new ArrayList<>());
            }

            dataMap.get(key).add(data);
        }
    }
}
