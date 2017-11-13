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

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ca.mudar.mtlaucasou.ConstDb.Tables;
import ca.mudar.mtlaucasou.model.LayerType;
import ca.mudar.mtlaucasou.model.MapType;
import ca.mudar.mtlaucasou.model.RealmPlacemark;

import static ca.mudar.mtlaucasou.ConstDb.Fields;

@Dao
public interface PlacemarkDao {
    @Query("SELECT * FROM " + Tables.PLACEMARKS)
    List<RealmPlacemark> getAll();

    @Query("SELECT *, props_name FROM " + Tables.PLACEMARKS +
            " WHERE ' ' || " + Fields.PROPERTIES_NAME + " COLLATE UNICODE LIKE :name")
    List<RealmPlacemark> getByName(String name);

    @Query("SELECT * FROM " + Tables.PLACEMARKS +
            " WHERE " + Fields.MAP_TYPE + " = :mapType")
    LiveData<List<RealmPlacemark>> getByMapType(@MapType String mapType);

    @Query("SELECT * FROM " + Tables.PLACEMARKS
            + " WHERE " + Fields.MAP_TYPE + " = :mapType AND " + Fields.LAYER_TYPE + " IN(:layerType)")
    LiveData<List<RealmPlacemark>> getByMapAndLayerType(@MapType String mapType,
                                                        @LayerType String[] layerType);

    @Insert
    long insert(RealmPlacemark placemark);

    @Query("DELETE FROM " + Tables.PLACEMARKS +
            " WHERE " + Fields.LAYER_TYPE + " IN(:layerType)")
    void deleteByLayerType(@LayerType String[] layerType);
}
