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

import com.google.android.gms.maps.model.LatLng;

import ca.mudar.mtlaucasou.Const;
import ca.mudar.mtlaucasou.model.geojson.PointsFeature;
import ca.mudar.mtlaucasou.util.ApiDataUtils;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

public class RealmPlacemark extends RealmObject implements
        Placemark {
    @Ignore
    public static final String FIELD_MAP_TYPE = "mapType";
    @Ignore
    public static final String FIELD_LAYER_TYPE = "layerType";
    @Ignore
    public static final String FIELD_COORDINATES = "coordinates";
    @Ignore
    public static final String FIELD_COORDINATES_LAT = "coordinates.lat";
    @Ignore
    public static final String FIELD_COORDINATES_LNG = "coordinates.lng";
    @Ignore
    public static final String FIELD_PROPERTIES_NAME = "properties.name";

    private String id;
    @MapType
    @Index
    private String mapType;
    @LayerType
    @Index
    private String layerType;
    private PlacemarkProperties properties;
    private LongitudeLatitude coordinates;

    public RealmPlacemark() {
        // Empty constructor
    }

    /**
     * Builder's constructor
     *
     * @param builder
     */
    private RealmPlacemark(Builder builder) {
        this.id = builder.id;
        this.mapType = builder.mapType;
        this.layerType = builder.layerType;
        this.properties = builder.properties;
        this.coordinates = builder.coordinates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return (getProperties() != null) ? getProperties().getName() : null;
    }

    @Override
    public String getDescription() {
        return (getProperties() != null) ? getProperties().getDescription() : null;
    }

    @Override
    public LatLng getLatLng() {
        return (getCoordinates() != null) ? getCoordinates().getLatLng() : null;
    }

    @Override
    @MapType
    public String getMapType() {
        return mapType;
    }

    public void setMapType(@MapType String mapType) {
        this.mapType = mapType;
    }

    @LayerType
    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(@LayerType String layerType) {
        this.layerType = layerType;
    }

    public PlacemarkProperties getProperties() {
        return properties;
    }

    public void setProperties(PlacemarkProperties properties) {
        this.properties = properties;
    }

    public LongitudeLatitude getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LongitudeLatitude coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Build a Realm Placemark from GeoJSON PointsFeature
     */
    public static class Builder {
        private String id;
        @MapType
        private String mapType;
        @LayerType
        private String layerType;
        private PlacemarkProperties properties;
        private LongitudeLatitude coordinates;

        public Builder(PointsFeature pointsFeature) {
            this.id = pointsFeature.getId();

            this.properties = new PlacemarkProperties.Builder(pointsFeature.getProperties())
                    .build();
            this.coordinates = new LongitudeLatitude.Builder(pointsFeature.getGeometry())
                    .build();
        }

        public Builder mapType(@MapType String mapType) {
            this.mapType = mapType;

            return this;
        }

        public Builder layerType(@LayerType String layerType, String dataType) {
            if (Const.LayerTypes._HEAT_WAVE_MIXED.equals(layerType)) {
                // water_supplies dataset is the only one with mixed layers
                this.layerType = ApiDataUtils.getPlacemarkLayerType(dataType);
            } else {
                this.layerType = layerType;
            }

            return this;
        }

        public RealmPlacemark build() {
            return new RealmPlacemark(this);
        }
    }
}