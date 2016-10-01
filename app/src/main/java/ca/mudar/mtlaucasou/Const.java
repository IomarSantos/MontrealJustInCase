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

package ca.mudar.mtlaucasou;

import android.os.Build;

import com.google.android.gms.maps.model.LatLng;

public class Const {
    /**
     * MTL au cas ou
     */
    public static final float MONTREAL_NATURAL_NORTH_ROTATION = -34f;
    public static final float ZOOM_IN = 16;
    public static final float ZOOM_DEFAULT = 14;
    public static final float ZOOM_OUT = 11;
    public static final LatLng MONTREAL_GEO_LAT_LNG = new LatLng(45.5d, -73.666667d);

    public interface MapTypes {
        String FIRE_HALLS = "fire_halls";
        String SPVM_STATIONS = "spvm_stations";
        String WATER_SUPPLIES = "water_supplies";
        String EMERGENCY_HOSTELS = "emergency_hostels";
        String HOSPITALS = "hospitals";
        String _DEFAULT = HOSPITALS;
    }

    public static final double MAPS_GEOCODER_LIMITS[] = {
            45.380127d, // lowerLeftLat
            -73.982620d, // lowerLeftLng
            45.720444d, // upperRightLat
            -73.466087d // upperRightLng
    };

    /**
     * GeoJSON OpenData API
     */
    public interface ApiPaths {
        // String BASE_URL = BuildConfig.API_BASE_URL; // "http://www.montrealaucasou.com/api/"
        String GET_FIRE_HALLS = "fire_halls.json";
        String GET_SPVM_STATIONS = "spvm_stations.json";
        String GET_WATER_SUPPLIES = "water_supplies.json";
        String GET_AIR_CONDITIONING = "air_conditioning.json";
        String GET_EMERGENCY_HOSTELS = "emergency_hostels.json";
        String GET_HOSPITALS = "hospitals.json";
    }

    public interface BundleKeys {
        String NAME = "name";
        String DESCRIPTION = "desc";
        String HAS_ACCEPTED_EULA = "has_accepted_eula";
    }

    /**
     * Minimum distance to center map on user location, otherwise center on
     * downtown. Units are meters.
     */
    public static final int MAPS_MIN_DISTANCE = 25000;

    public interface UnitsDisplay {
        float FEET_PER_MILE = 5280f;
        float METER_PER_MILE = 1609.344f;
        int ACCURACY_FEET_FAR = 100;
        int ACCURACY_FEET_NEAR = 10;
        int MIN_FEET = 200;
        int MIN_METERS = 100;
    }


    /**
     * Database
     */
    public static final String DATABASE_NAME = "mtlaucasou.realm";
    public static final int DATABASE_VERSION = 10;

    /**
     * Settings, SharedPreferences
     */
    public static final String APP_PREFS_NAME = "MTL_JUSTINCASE_PREFS";

    public interface PrefsNames {
        String HAS_LOADED_DATA_LEGACY = "prefs_has_loaded_data"; // version 1.0
        String HAS_LOADED_DATA = "prefs_has_loaded_data_v2";
        String IS_FIRST_LAUNCH = "prefs_is_first_launch";
        String HAS_ACCEPTED_EULA = "accepted_eula";
//        String VERSION_DATABASE = "prefs_version_database";
        String LANGUAGE = "prefs_language";
        String PERMISSIONS = "prefs_permissions";
        String UNITS_SYSTEM = "prefs_units_system";
//        String LIST_SORT = "prefs_list_sort_by";
//        String FOLLOW_LOCATION_CHANGES = "prefs_follow_location_changes";
        String LAST_UPDATE_TIME = "prefs_last_update_time";
        String LAST_UPDATE_LAT = "prefs_last_update_lat";
        String LAST_UPDATE_LNG = "prefs_last_update_lng";
        String PERMISSION_DENIED_FOR_EVER = "prefs_permission_denied";
    }

    public interface PrefsValues {
        String LANG_FR = "fr";
        String LANG_EN = "en";
        String UNITS_ISO = "iso";
        String UNITS_IMP = "imp";
//        String LIST_SORT_NAME = "name";
//        String LIST_SORT_DISTANCE = "distance";
    }

    public interface RequestCodes {
        int EULA_ACCEPTED = 10;
        int LOCATION_PERMISSION = 20;
        int LOCATION_SETTINGS_CHANGE = 30;
    }

    public interface FragmentTags {
        String SETTINGS = "fragment_settings";
        String MAP = "fragment_map";
        String DIALOG_OD_CREDITS = "dialog_od_credits";
    }

    /**
     * Other constants
     */
    public static final int UNKNOWN_VALUE = -1;
//    public static final long ANIM_SHORT_DURATION = 200L;
//    public static final long ANIM_MEDIUM_DURATION = 400L;
//    public static final long ANIM_LONG_DURATION = 500L;
    public static final String CUSTOM_LOCATION_PROVIDER = "search_provider";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    @Deprecated
    public static final String HTML_LINE_SEPARATOR = "<br>";

    // Assets
    public interface LocalAssets {
        String LICENSE = "gpl-3.0-standalone.html";
    }

    /**
     * Compatibility
     */
    public static final boolean SUPPORTS_NOUGAT = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
}
