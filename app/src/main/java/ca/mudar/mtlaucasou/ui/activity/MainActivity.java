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

package ca.mudar.mtlaucasou.ui.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.List;

import ca.mudar.mtlaucasou.Const;
import ca.mudar.mtlaucasou.R;
import ca.mudar.mtlaucasou.data.AppDatabase;
import ca.mudar.mtlaucasou.data.RoomQueries;
import ca.mudar.mtlaucasou.data.UserPrefs;
import ca.mudar.mtlaucasou.model.MapType;
import ca.mudar.mtlaucasou.model.Placemark;
import ca.mudar.mtlaucasou.model.RoomPlacemark;
import ca.mudar.mtlaucasou.model.RoomPolygon;
import ca.mudar.mtlaucasou.ui.adapter.PlacemarkInfoWindowAdapter;
import ca.mudar.mtlaucasou.ui.listener.LocationUpdatesManager;
import ca.mudar.mtlaucasou.ui.listener.MapLayersManager;
import ca.mudar.mtlaucasou.ui.listener.SearchResultsManager;
import ca.mudar.mtlaucasou.ui.view.PlacemarksSearchView;
import ca.mudar.mtlaucasou.util.EulaUtils;
import ca.mudar.mtlaucasou.util.LogUtils;
import ca.mudar.mtlaucasou.util.MapUtils;
import ca.mudar.mtlaucasou.util.MetricsUtils;
import ca.mudar.mtlaucasou.util.NavigUtils;
import ca.mudar.mtlaucasou.util.PermissionUtils;

import static ca.mudar.mtlaucasou.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        SearchResultsManager.MapUpdatesListener,
        LocationUpdatesManager.LocationUpdatesCallbacks,
        MapLayersManager.LayersFilterCallbacks {

    private static final String TAG = makeLogTag("MainActivity");
    private static final long BOTTOM_NAV_ANIM_DURATION = 200L; // 200ms
    private static final long PROGRESS_BAR_ANIM_DURATION = 750L; // 750ms

    private GoogleMap vMap;
    private View mSnackbarParent;
    private CircleProgressBar vProgressBar;
    private FloatingActionButton mMyLocationFAB;
    private MapLayersManager mLayersManager;
    private BottomNavigationView mBottomNav;
    @MapType
    private String mMapType;
    private AppDatabase mDb;
    private Handler mHandler = new Handler(); // Waits for the BottomNav anim
    private LocationUpdatesManager mLocationManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Avoid showing EULA twice on orientation change
            EulaUtils.showEulaIfNecessary(this);
        }

        setTitle(R.string.title_activity_main);
        setContentView(R.layout.activity_main);

        vProgressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        mSnackbarParent = findViewById(R.id.map_wrapper);

        mDb = AppDatabase.getAppDatabase(getApplicationContext());

        mLocationManger = new LocationUpdatesManager(MainActivity.this, this);

        setupMap();
        setupFAB();

        final @MapType String lastMapType = UserPrefs.getInstance(getApplicationContext()).getLastMapType();
        setupBottomNav(lastMapType);
        setMapType(lastMapType, 0);
    }

    protected void onStart() {
        super.onStart();

        mLocationManger.onStart();

        toggleMyLocationButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        setupSearchView(menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            mLocationManger.onStop();

            UserPrefs.getInstance(getApplicationContext()).setLastMapType(mMapType);
        } catch (Exception e) {
            LogUtils.REMOTE_LOG(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppDatabase.destroyInstance();
        } catch (Exception e) {
            LogUtils.REMOTE_LOG(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Const.RequestCodes.LOCATION_SETTINGS_CHANGE == requestCode) {
            onLocationSettingsActivityResult(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Const.RequestCodes.LOCATION_PERMISSION) {
            if (PermissionUtils.checkLocationPermission(this)) {
                UserPrefs.getInstance(getApplicationContext()).setPermissionDeniedForEver(true);

                mMyLocationFAB.show();
                MapUtils.enableMyLocation(this, vMap);
                mLocationManger.onLocationPermissionGranted();
            } else {
                PermissionUtils.showLocationRationaleOrSurrender(this, mSnackbarParent);

                mMyLocationFAB.hide();
                mMyLocationFAB.setVisibility(View.GONE);
            }
        }
    }

    private void setupSearchView(final Menu menu) {
        // Get the toolbar menu SearchView
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        final PlacemarksSearchView searchView =
                (PlacemarksSearchView) MenuItemCompat.getActionView(searchMenuItem);

        try {
            searchView.setSearchMenuItem(searchMenuItem);
            searchView.setListener(new SearchResultsManager(MainActivity.this, this));
        } catch (Exception e) {
            LogUtils.REMOTE_LOG(e);
            searchMenuItem.setVisible(false);
        }
    }

    /**
     * Show the bottom bar navigation items
     */
    private void setupBottomNav(final @MapType String type) {
        mBottomNav = findViewById(R.id.bottom_nav);
        assert mBottomNav != null;
        mBottomNav.setSelectedItemId(NavigUtils.getTabIdByMapType(type));
        mBottomNav.setBackgroundResource(NavigUtils.getMapTypeColor(type));

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                @IdRes final int itemId = item.getItemId();

                final @MapType String mapType = NavigUtils.getMapTypeByTabId(itemId);
                mBottomNav.setBackgroundResource(NavigUtils.getMapTypeColor(mapType));
                showcaseMapLayers(mapType);
                setMapType(mapType, BOTTOM_NAV_ANIM_DURATION);

                return true;
            }
        });

        mBottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if (isMapReady(false)) {
                    vMap.animateCamera(CameraUpdateFactory.zoomTo(Const.ZOOM_OUT));
                }
                mLayersManager.toggleFilterMenu();
            }
        });
    }

    private void setupFAB() {
        mMyLocationFAB = (FloatingActionButton) findViewById(R.id.fab);
        mMyLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.checkLocationPermission(MainActivity.this)) {
                    MapUtils.moveCameraToLocation(vMap, mLocationManger.getUserLocation(), true);
                } else {
                    PermissionUtils.requestLocationPermission(MainActivity.this);
                }
            }
        });

        mLayersManager = new MapLayersManager(this, (FloatingActionMenu) findViewById(R.id.fab_menu), this);
    }

    /**
     * Obtain the SupportMapFragment and get notified when the map is ready to be used.
     */
    private void setupMap() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentByTag(Const.FragmentTags.MAP);
            if (mapFragment == null) {
                mapFragment = new SupportMapFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mapFragment, Const.FragmentTags.MAP)
                    .commit();

            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            LogUtils.REMOTE_LOG(e);
            isMapReady(true);
        }
    }

    /**
     * Manipulate the map once available.
     *
     * @param googleMap The GoogleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        vMap = googleMap;
        vMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Setup the InfoWindow
        @SuppressLint("InflateParams") final View vMarkerInfoWindow = getLayoutInflater()
                .inflate(R.layout.custom_info_window, null, false);
        vMap.setInfoWindowAdapter(new PlacemarkInfoWindowAdapter(vMarkerInfoWindow, mLocationManger));

        // Handle location
        MapUtils.setupMapLocation(vMap);
        MapUtils.enableMyLocation(this, vMap);

        mLocationManger.setGoogleMap(vMap);

        loadMapData(mMapType);

        mLayersManager.setMap(vMap);
    }

    /**
     * Verify if GoogleMap has loaded correctly.
     * Utility method to handle a Google Play Services error
     * Ref: https://code.google.com/p/gmaps-api-issues/issues/detail?id=5100
     *
     * @return true if onMapReady() has been successfully called
     */
    private boolean isMapReady(boolean showError) {
        if (vMap == null) {
            if (showError) {
                Snackbar.make(mSnackbarParent, R.string.snackbar_google_maps_error, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setupMap();
                            }
                        })
                        .show();
            }
            return false;
        }

        return true;
    }

    private void setMapType(final @MapType String type, long delay) {
        mMapType = type;

        // return value is not used
        final boolean hasFilterMenu = mLayersManager.toggleFilterMenu(type);

        if (isMapReady(false)) {
            toggleProgressBar(true);

            mHandler.removeCallbacksAndMessages(null);
            // Wait for the BottomBar animation to end before loading data
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Map was already cleared, to show user something is happening!
                    loadMapData(type);
                }
            }, delay);
        }

        MetricsUtils.logMapView(type);
    }

    /**
     * Load the cached data, or request download
     *
     * @param type
     */
    private void loadMapData(@MapType final String type) {
        if (!isMapReady(true)) {
            return;
        }

        // Query the Room db for the current mapType
        RoomQueries.queryPlacemarksByMapType(
                mDb,
                type,
                UserPrefs.getInstance(getApplicationContext()).getEnabledLayers()
        ).observe(this, new Observer<List<RoomPlacemark>>() {
            @Override
            public void onChanged(@Nullable List<RoomPlacemark> roomPlacemarks) {
                if (roomPlacemarks != null && roomPlacemarks.size() > 0) {
                    // Has cached data
                    final List<Marker> markers = MapUtils.addPlacemarksToMap(vMap, roomPlacemarks);

                    // Save markers IDs for onClick usage
                    mLayersManager.saveMarkersIdsIfNecessary(type, markers, roomPlacemarks);

                    new Handler().postDelayed(new Runnable() {
                        /**
                         * Delay hiding the progressbar for 750ms, avoids blink-effect on fast operations.
                         * And allows findAndShowNearestMarker() to wait for the real center in case
                         * of camera animation.
                         */
                        @Override
                        public void run() {
                            toggleProgressBar(false);
                            MapUtils.findAndShowNearestMarker(vMap, markers, mSnackbarParent);
                        }
                    }, PROGRESS_BAR_ANIM_DURATION);
                }
            }
        });
    }

    @Override
    public void moveCameraToPlacemark(Placemark placemark) {
        GoogleMap.OnCameraIdleListener cameraIdleListener;

        // Enable this placemark's layer if necessary
        final UserPrefs prefs = UserPrefs.getInstance(getApplicationContext());
        final boolean updateLayers = !prefs.isLayerEnabled(placemark.getLayerType());
        if (updateLayers) {
            prefs.setLayerEnabledForced(placemark.getMapType(), placemark.getLayerType());
            mLayersManager.setupEnabledLayers(prefs);
        }

        if (mMapType != null && !mMapType.equals(placemark.getMapType())) {
            final int tabId = NavigUtils.getTabIdByMapType(placemark.getMapType());

            cameraIdleListener = new GoogleMap.OnCameraIdleListener() {
                /**
                 * We need to switch mapType after the camera animation. Selecting the tab
                 * triggers a call to setMapType() which clears map and loads data
                 */
                @Override
                public void onCameraIdle() {
                    mBottomNav.setSelectedItemId(tabId);
                }
            };
        } else if (updateLayers) {
            cameraIdleListener = new GoogleMap.OnCameraIdleListener() {
                /**
                 * After the camera animation, we need to
                 */
                @Override
                public void onCameraIdle() {
                    onFiltersApply();
                }
            };
        } else {
            // Selected placemark is of current type: ignore
            cameraIdleListener = null;
        }

        MapUtils.moveCameraToPlacemark(vMap, placemark, true, cameraIdleListener);
    }

    @Override
    public void moveCameraToLocation(Location location) {
        MapUtils.moveCameraToLocation(vMap, location, true);
    }

    private void toggleProgressBar(boolean visible) {
        if (visible) {
            vProgressBar.setColorSchemeColors(MapUtils.getMapTypeColor(this, mMapType));
            vProgressBar.setVisibility(View.VISIBLE);
        } else {
            vProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Verify if user has changed his mind about denying permission forever.
     * This method uses setVisibility() instead of show/hide methods.
     */
    private void toggleMyLocationButton() {
        if (PermissionUtils.checkPermissionWasDeniedForEver(getApplicationContext())) {
            mMyLocationFAB.hide();
        } else {
            mMyLocationFAB.show();
        }
    }

    /**
     * Implements LocationUpdatesManager.LocationUpdatesCallbacks
     *
     * @param status
     * @throws IntentSender.SendIntentException
     */
    @Override
    public void requestLocationSettingsChange(Status status)
            throws IntentSender.SendIntentException {
        status.startResolutionForResult(
                MainActivity.this,
                Const.RequestCodes.LOCATION_SETTINGS_CHANGE);
    }

    /**
     * Implements LocationUpdatesManager.LocationUpdatesCallbacks
     */
    @Override
    public void onLocationSettingsActivityResult(int resultCode, Intent data) {
        mLocationManger.onLocationSettingsResult(resultCode, data);
    }

    /**
     * MapLayersManager.LayersFilterCallbacks
     */
    @Override
    public void onFiltersChange() {
        MapUtils.clearMap(vMap);
    }

    /**
     * MapLayersManager.LayersFilterCallbacks
     */
    @Override
    public void onFiltersApply() {
        toggleProgressBar(true);

        loadMapData(mMapType);
    }

    /**
     * MapLayersManager.LayersFilterCallbacks
     */
    @Override
    public void onPlacemarkClick(long placemarkId) {
        RoomQueries.queryPolygonsByPlacemarkId(mDb, placemarkId)
                .observe(this, new Observer<List<RoomPolygon>>() {
                    @Override
                    public void onChanged(@Nullable List<RoomPolygon> roomPolygons) {
                        if (roomPolygons != null && roomPolygons.size() > 0) {
                            final List<Polygon> polygons = MapUtils
                                    .addPolygonsToMap(MainActivity.this, vMap, roomPolygons);

                            mLayersManager.togglePolygonsLayer(polygons);
                        }
                    }
                });
    }
}
