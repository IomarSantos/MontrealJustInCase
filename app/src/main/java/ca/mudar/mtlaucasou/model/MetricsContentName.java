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

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        MapType.FIRE_HALLS,
        MapType.SPVM_STATIONS,
        MapType.HEAT_WAVE,
        MapType.EMERGENCY_HOSTELS,
        MapType.HEALTH,
        MetricsContentName.SETTINGS,
        MetricsContentName.ABOUT,
        MetricsContentName.ABOUT_LIBS,
        MetricsContentName.EULA,
})
public @interface MetricsContentName {
    String SETTINGS = "SettingsActivity";
    String ABOUT = "AboutActivity";
    String ABOUT_LIBS = "AboutLibsActivity";
    String EULA = "EulaActivity";
}