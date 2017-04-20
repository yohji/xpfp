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

package net.marcomerli.xpfms.model;

import com.google.maps.model.LatLng;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Location extends LatLng {

	public Location(double lat, double lng) {

		super(lat, lng);
	}

	public String getLatitude()
	{
		return String.format("%s%s",
			Math.abs(lat), (lat > 0) ? "N" : "S");
	}

	public String getLongitude()
	{
		return String.format("%s%s",
			Math.abs(lng), (lng > 0) ? "E" : "W");
	}

	public String getCoordinate()
	{
		return getLatitude() + " " + getLongitude();
	}
}
