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

import java.util.Iterator;
import java.util.LinkedList;

import net.marcomerli.xpfp.error.NoSuchWaypointException;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FlightPlan extends LinkedList<Waypoint> {

	private static final long serialVersionUID = 1776920015546695648L;

	private String name;
	private Double distance = 0.0;
	private Long ete = 0L;

	public FlightPlan(String name) {

		this.name = name;
	}

	public void calculate(double fl, double cs, double vs)
	{
		Iterator<Waypoint> iterator = iterator();
		Waypoint prev = iterator.next();

		double climb = fl - prev.getLocation().alt;
		double sec = climb / vs;
		double dist = (cs * 0.6) * sec;

		for (; iterator.hasNext();) {
			Waypoint wp = iterator.next();

			if (! iterator.hasNext()) // Destination
				break;

			wp.getLocation().alt = fl;
			// TODO: continue...

			prev = wp;
		}
	}

	public void setup()
	{
		Iterator<Waypoint> iterator = iterator();
		Waypoint prev = iterator.next();

		for (; iterator.hasNext();) {
			Waypoint wp = iterator.next();

			distance += wp.setDistance(prev);
			wp.setBearing(prev);

			prev = wp;
		}
	}

	public Waypoint getDeparture() throws NoSuchWaypointException
	{
		Waypoint wp = getFirst();
		if (! wp.getType().equals(WaypointType.ICAO))
			throw new NoSuchWaypointException("Departure not specified.");

		return wp;
	}

	public Waypoint getDestination() throws NoSuchWaypointException
	{
		Waypoint wp = getLast();
		if (! wp.getType().equals(WaypointType.ICAO))
			throw new NoSuchWaypointException("Destination not specified.");

		return wp;
	}

	public String getFilename()
	{
		return String.format("%s.fms",
			name.replaceAll("\\W+", "_"));
	}

	public String getName()
	{
		return name;
	}

	public void setDistance(Double distance)
	{
		this.distance = distance;
	}

	public Double getDistance()
	{
		return distance;
	}

	public Long getEte()
	{
		return ete;
	}
}
