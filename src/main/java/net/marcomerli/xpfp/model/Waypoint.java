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

import net.marcomerli.xpfp.fn.GeoFn;
import net.marcomerli.xpfp.fn.NumberFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Waypoint implements Serializable {

	private static final long serialVersionUID = 5152303308237206301L;

	private String identifier;
	private WaypointType type;
	private Location location;
	private String country;

	private Double distance = 0.0;
	private Long ete = 0L;
	private Integer bearing;

	public Double setDistance(Waypoint wp)
	{
		return (distance = GeoFn.distanceOf(
			wp.getLocation(), this.getLocation()));
	}

	public Integer setBearing(Waypoint wp)
	{
		return (bearing = GeoFn.bearingOf(
			wp.getLocation(), this.getLocation()));
	}

	public Double getDistance()
	{
		return distance;
	}

	public Long getEte()
	{
		return ete;
	}

	public Integer getBearing()
	{
		return bearing;
	}

	public String getFMSIdentifier()
	{
		if (type.equals(WaypointType.POS))
			return String.format("%s%s_%s%s",
				(location.lat > 0 ? "+" : "-"),
				NumberFn.format(location.lat, 3),
				(location.lng > 0 ? "+" : "-"),
				NumberFn.format(location.lng, 3));

		return identifier;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public WaypointType getType()
	{
		return type;
	}

	public void setType(WaypointType type)
	{
		this.type = type;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}
}
