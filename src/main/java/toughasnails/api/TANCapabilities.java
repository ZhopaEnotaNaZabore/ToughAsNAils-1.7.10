/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package toughasnails.api;

import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;

public class TANCapabilities 
{
    public static final String TEMPERATURE = ITemperature.class.getName();
    public static final String THIRST = IThirst.class.getName();
}
