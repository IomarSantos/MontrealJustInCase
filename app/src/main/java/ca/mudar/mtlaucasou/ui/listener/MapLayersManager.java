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

package ca.mudar.mtlaucasou.ui.listener;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashSet;
import java.util.Set;

import ca.mudar.mtlaucasou.Const;
import ca.mudar.mtlaucasou.Const.MapTypes;
import ca.mudar.mtlaucasou.R;
import ca.mudar.mtlaucasou.data.UserPrefs;
import ca.mudar.mtlaucasou.model.MapType;
import ca.mudar.mtlaucasou.util.MapUtils;

import static ca.mudar.mtlaucasou.util.LogUtils.makeLogTag;

public class MapLayersManager implements
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener {
    private static final String TAG = makeLogTag("MapLayersManager");

    private final Context mContext;
    private final FloatingActionMenu mMenuFAB;
    private final FloatingActionButton mAirConditioningFAB;
    private final FloatingActionButton mPoolsFAB;
    private final FloatingActionButton mWadingPoolsFAB;
    private final FloatingActionButton mPlayFountainsFAB;
    private final FloatingActionButton mHospitalsFAB;
    private final FloatingActionButton mClscFAB;
    private Set<String> mActiveLayers;
    private boolean mMapTypeHasMenu;
    @ColorInt
    private int mMapTypeColor;
    @ColorInt
    private int mNormalColor;

    public MapLayersManager(@NonNull Context context, @NonNull FloatingActionMenu menu) {
        mContext = context;
        mMenuFAB = menu;
        mMenuFAB.setIconAnimated(false);
        mMenuFAB.hideMenu(false);
        ViewCompat.setElevation(mMenuFAB,
                mContext.getResources().getDimensionPixelSize(R.dimen.fab_menu_elevation));

        mActiveLayers = new HashSet<>();

        mNormalColor = ContextCompat.getColor(mContext, R.color.fab_menu_item_color_normal);

        // The menu items
        mAirConditioningFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_air_conditioning);
        mPoolsFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_pools);
        mWadingPoolsFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_wading_pools);
        mPlayFountainsFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_play_fountains);
        mHospitalsFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_hospitals);
        mClscFAB = (FloatingActionButton) mMenuFAB.findViewById(R.id.fab_clsc);

        setupInitialValues(UserPrefs.getInstance(context));
        setupMenuItemsListeners();
    }

    /**
     * Provide reference to the GoogleMap to setup click listeners
     *
     * @param map The GoogleMap
     */
    public void setMap(GoogleMap map) {
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    /**
     * Re-selecting tabs hides/shows the menu
     */
    public void toggleFilterMenu() {
        if (mMapTypeHasMenu) {
            mMenuFAB.toggleMenu(true);
        }
    }

    /**
     * Clicking the map hides/shows the menu
     * Implements GoogleMap.OnMapClickListener
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if (mMapTypeHasMenu) {
            mMenuFAB.toggleMenu(true);
        }
    }

    /**
     * Clicking a marker hides the menu to allow display of the MapToolbar (directions/GMaps).
     * Implements GoogleMap.OnMarkerClickListener
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMapTypeHasMenu) {
            mMenuFAB.hideMenu(true);
        }
        return false;
    }

    /**
     * Implements View.OnClickListener
     *
     * @param view
     */
    @Override
    public void onClick(final View view) {
        if (view instanceof FloatingActionButton) {
            final boolean isActivated = !view.isActivated(); // The new (toggled) value
            setMenuItemState((FloatingActionButton) view, isActivated);
            UserPrefs.getInstance(mContext).setLayerEnabled(
                    MapUtils.getFilterItemLayerType(view.getId()),
                    isActivated);
        }
    }

    /**
     * Toggle the visibility of the FloatingActionMenu based on the map type.
     *
     * @param type Selected map type {fire_halls|spvm_stations|water_supplies|emergency_hostels|hospitals}
     * @return true if map type has a FloatingActionMenu
     */
    public boolean toggleFilterMenu(@MapType String type) {
        mMapTypeHasMenu = MapTypes.HEAT_WAVE.equals(type) || MapTypes.HEALTH.equals(type);

        toggleWaterSupplyFilterItems(MapTypes.HEAT_WAVE.equals(type));
        toggleHospitalsFilterItems(MapTypes.HEALTH.equals(type));

        if (mMapTypeHasMenu) {
            // Initial visibility is GONE to avoid visual flickering
            mMenuFAB.setVisibility(View.VISIBLE);
            mMenuFAB.showMenu(true);

            setupFilterMenuStyle(type);
        } else {
            mMenuFAB.hideMenu(true);
        }

        return mMapTypeHasMenu;
    }

    private void toggleWaterSupplyFilterItems(boolean visible) {
        final int visibilty = visible ? View.VISIBLE : View.GONE;

        // Enable the buttons visibility
        mAirConditioningFAB.setVisibility(visibilty);
        mPoolsFAB.setVisibility(visibilty);
        mWadingPoolsFAB.setVisibility(visibilty);
        mPlayFountainsFAB.setVisibility(visibilty);

        if (visible && !mMenuFAB.isOpened()) {
            // Hide the buttons till the menu is opened
            mAirConditioningFAB.hide(false);
            mPoolsFAB.hide(false);
            mWadingPoolsFAB.hide(false);
            mPlayFountainsFAB.hide(false);
        }
    }

    private void toggleHospitalsFilterItems(boolean visible) {
        final int visibilty = visible ? View.VISIBLE : View.GONE;

        // Enable the buttons visibility
        mHospitalsFAB.setVisibility(visibilty);
        mClscFAB.setVisibility(visibilty);

        if (visible && !mMenuFAB.isOpened()) {
            // Hide the buttons till the menu is opened
            mHospitalsFAB.hide(false);
            mClscFAB.hide(false);
        }
    }

    private void setupFilterMenuStyle(@MapType String type) {
        mMapTypeColor = MapUtils.getMapTypeColor(mContext, type);
        mMenuFAB.setMenuButtonColorNormal(mMapTypeColor);
        mMenuFAB.setMenuButtonColorPressed(mMapTypeColor);
    }

    private void setupInitialValues(UserPrefs prefs) {
        final Set<String> enabledLayers = prefs.getEnabledLayers();

        mMapTypeColor = MapUtils.getMapTypeColor(mContext, MapTypes.HEAT_WAVE);
        setMenuItemState(mAirConditioningFAB, enabledLayers.contains(Const.LayerTypes.AIR_CONDITIONING));
        setMenuItemState(mPoolsFAB, enabledLayers.contains(Const.LayerTypes.POOLS));
        setMenuItemState(mWadingPoolsFAB, enabledLayers.contains(Const.LayerTypes.WADING_POOLS));
        setMenuItemState(mPlayFountainsFAB, enabledLayers.contains(Const.LayerTypes.PLAY_FOUNTAINS));

        mMapTypeColor = MapUtils.getMapTypeColor(mContext, MapTypes.HEALTH);
        setMenuItemState(mHospitalsFAB, enabledLayers.contains(Const.LayerTypes.HOSPITALS));
        setMenuItemState(mClscFAB, enabledLayers.contains(Const.LayerTypes.CLSC));

        mMapTypeColor = mNormalColor;
    }

    private void setupMenuItemsListeners() {
        mAirConditioningFAB.setOnClickListener(this);
        mPoolsFAB.setOnClickListener(this);
        mWadingPoolsFAB.setOnClickListener(this);
        mPlayFountainsFAB.setOnClickListener(this);
        mHospitalsFAB.setOnClickListener(this);
        mClscFAB.setOnClickListener(this);
    }

    private void setMenuItemState(FloatingActionButton fab, boolean activated) {
        @ColorInt final int color = activated ? mMapTypeColor : mNormalColor;
        fab.setActivated(activated);
        fab.setColorNormal(color);
    }
}
