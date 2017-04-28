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

import java.util.Iterator;
import java.util.LinkedList;

import net.marcomerli.xpfp.error.NoSuchWaypointException;
import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.fn.GeoFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FlightPlan extends LinkedList<Waypoint> {

	private static final long serialVersionUID = 1776920015546695648L;

	private String name;
	private String filename;
	private Double distance = 0.0;
	private Long ete = 0L;

	public FlightPlan(String name) {

		this.name = name;
		filename = name.replaceAll("\\W+", "_");
	}

	public void calculate(double fl, double cs) throws Exception
	{
		/*
		 * TODO: implement vertical calculation
		 * 
		 * double dAlt = fl - loc.alt;
		 * double secAlt = dAlt / vs;
		 * double nmAlt = (cs * 0.6) * secAlt;
		 */

		distance = 0.0;
		ete = 0L;

		Iterator<Waypoint> iterator = iterator();
		Waypoint prev = iterator.next();
		Location loc = prev.getLocation();
		GeoFn.elevation(loc);

		for (; iterator.hasNext();) {
			Waypoint wp = iterator.next();

			Double dist = wp.setDistance(prev);
			wp.setCourse(prev);
			ete += wp.setEte((long) ((double) dist / cs) * 1000);

			loc = wp.getLocation();
			if (wp.getType().equals(WaypointType.ICAO))
				GeoFn.elevation(loc);
			else
				loc.alt = fl;

			distance += dist;
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

	public String getFullFilename(FileType type)
	{
		return filename + "." + type.extension();
	}

	public String getName()
	{
		return name;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
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
