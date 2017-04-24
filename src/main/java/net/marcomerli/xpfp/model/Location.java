/**
 *  Copyright (c) 2017 Marco Merli <yohji@marcomerli.net>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package net.marcomerli.xpfp.model;

import java.io.Serializable;

import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;

import com.google.maps.model.LatLng;

import net.marcomerli.xpfp.fn.NumberFn;
import net.marcomerli.xpfp.fn.UnitFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Location extends LatLng implements Serializable {

	private static final long serialVersionUID = - 5324412533678066003L;

	public double alt;

	private Latitude latitude;
	private Longitude longiture;

	public Location(double lat, double lng, double alt) {

		super(lat, lng);
		this.alt = alt;

		latitude = new Latitude(lat);
		longiture = new Longitude(lng);
	}

	public Location(double lat, double lng) {

		this(lat, lng, 0);
	}

	public String getLatitude()
	{
		return latitude.toString();
	}

	public String getLongitude()
	{
		return longiture.toString();
	}

	public String getAltitude()
	{
		return (alt > 0 ? NumberFn.format(UnitFn.mToFt(alt), 2) + " ft" : "-");
	}
}
