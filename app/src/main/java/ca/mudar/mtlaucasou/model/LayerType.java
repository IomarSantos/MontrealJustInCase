/*
    Montréal Just in Case
    Copyright (C) 2011  Mudar Noufal <mn@mudar.ca>

    Geographic locations of public safety services. A Montréal Open Data
    project.

    This file is part of Montréal Just in Case.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.mtlaucasou.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ca.mudar.mtlaucasou.Const;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        Const.LayerTypes.FIRE_HALLS,
        Const.LayerTypes.SPVM_STATIONS,
        Const.LayerTypes.EMERGENCY_HOSTELS,
        // Heat wave x4
        Const.LayerTypes.AIR_CONDITIONING,
        Const.LayerTypes.POOLS,
        Const.LayerTypes.WADING_POOLS,
        Const.LayerTypes.PLAY_FOUNTAINS,
        Const.LayerTypes._HEAT_WAVE_MIXED,
        // Health x2
        Const.LayerTypes.HOSPITALS,
        Const.LayerTypes.CLSC
})
public @interface LayerType {
}