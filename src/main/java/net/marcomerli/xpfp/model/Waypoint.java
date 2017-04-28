/**
 *   Copyright (c) 2017 Marco Merli <yohji@marcomerli.net>
 *   This file is part of XPFP.
 *
 *   XPFP is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   XPFP is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with XPFP.  If not, see <http://www.gnu.org/licenses/>.
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
	private Double bearing;
	private Double heading;

	public Double setDistance(Waypoint wp)
	{
		return (distance = GeoFn.distance(
			wp.getLocation(), this.getLocation()));
	}

	public void setCourse(Waypoint wp)
	{
		bearing = GeoFn.bearing(wp.getLocation(), this.getLocation());
		heading = bearing + GeoFn.declination(wp.getLocation());
	}

	public Long setEte(Long ete)
	{
		return (this.ete = ete);
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

	public Double getDistance()
	{
		return distance;
	}

	public Long getEte()
	{
		return ete;
	}

	public Double getBearing()
	{
		return bearing;
	}

	public Double getHeading()
	{
		return heading;
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
