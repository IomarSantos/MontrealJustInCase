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

package ca.mudar.mtlaucasou.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ca.mudar.mtlaucasou.Const;
import ca.mudar.mtlaucasou.R;

import static ca.mudar.mtlaucasou.util.LogUtils.makeLogTag;

public class UserPrefs implements
        Const.PrefsNames {
    private static final String TAG = makeLogTag("Settings");
    private static UserPrefs instance;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    public static UserPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new UserPrefs(context);
        }
        return instance;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void setDefaults(Context context) {
        PreferenceManager.setDefaultValues(context,
                Const.APP_PREFS_NAME,
                Context.MODE_PRIVATE,
                R.xml.prefs_defaults,
                false);
    }

    private UserPrefs(Context context) {
        mPrefs = getPrefs(context);
    }

    private SharedPreferences.Editor edit() {
        if (mPrefsEditor == null) {
            mPrefsEditor = mPrefs.edit();
        }

        return mPrefsEditor;
    }

    public boolean isFirstLaunch() {
        return mPrefs.getBoolean(HAS_LOADED_DATA, false);
    }

    public void setFirstLaunch() {
        edit().putBoolean(HAS_LOADED_DATA, true)
                .commit();
    }

    public String getLanguage() {
        return mPrefs.getString(LANGUAGE, null);
    }

    public void setLanguage(String language) {
        edit().putString(LANGUAGE, language)
                .commit();
    }

    public String getUnitsSystem() {
        return mPrefs.getString(UNITS_SYSTEM, null);
    }

    public void setUnitsSystem(String units) {
        edit().putString(UNITS_SYSTEM, units)
                .apply();
    }
}
