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
import net.marcomerli.xpfp.fn.UnitFn;

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

	public void _calculate(final double crzAlt, final double crzSpeed) throws Exception
	{
		//
		final double clbSpeed = UnitFn.knToMs(75);
		final double clbRate = UnitFn.ftToM(1250);
		//

		distance = 0.0;
		ete = 0L;

		for (Iterator<Waypoint> it = iterator(); it.hasNext();)
			if (it.next().isCalculated())
				it.remove();

		Waypoint dep = getDeparture();
		Location depLoc = dep.getLocation();
		GeoFn.elevation(depLoc);

		boolean clbWpAdd = false;
		double clbAlt = crzAlt - depLoc.alt;
		double clbTime = (clbAlt / clbRate) * 60;
		double clbDistance = Math.sqrt(
			Math.pow(clbSpeed * clbTime, 2) - Math.pow(clbAlt, 2));

		for (int iWp = 1; iWp < size(); iWp++) {
			Waypoint prev = get(iWp - 1);
			Waypoint curr = get(iWp);

			if (curr.getType().equals(WaypointType.ICAO))
				GeoFn.elevation(curr.getLocation());

			curr.getLocation().alt = crzAlt;
			curr.setCourse(prev);
			double currDist = curr.setDistance(prev);
			long currEte = curr.setEte(crzSpeed);
			double sumDistance = (distance != 0 ? distance : currDist);

			if (! clbWpAdd && sumDistance > clbDistance) {

				Waypoint clbWp = new Waypoint();
				clbWp.setIdentifier("ʌʌʌ");
				clbWp.setCalculated(true);
				clbWp.setType(WaypointType.POS);
				clbWp.setLocation(GeoFn.point(
					prev.getLocation(),
					(sumDistance - clbDistance),
					curr.getBearing()));

				for (int iFix = (iWp - 1); iFix > 0; iFix--) {
					// FIXME: mid altitudes and ETE
					Waypoint currFix = get(iFix);
					currFix.getLocation().alt = - 1;
				}

				clbWp.getLocation().alt = crzAlt;
				clbWp.setCourse(prev);
				clbWp.setDistance(prev);
				clbWp.setEte(clbSpeed);

				curr.setDistance(clbWp);
				curr.setEte(crzSpeed);

				add(iWp, clbWp);
				iWp += 1;
				clbWpAdd = true;
			}
			else {
				distance += currDist;
				ete += currEte;
			}
		}

		/*
		 * FIXME: calculate descent (ṿṿṿ)
		 * 
		 * final double desSpeed = UnitFn.knToMs(120);
		 * final double desRate = UnitFn.ftToM(1500);
		 * 
		 * Waypoint arr = getArrival();
		 * Location arrLoc = arr.getLocation();
		 *
		 * double desAlt = fl - depLoc.alt;
		 * double desTime = desAlt / (desRate / 60);
		 * double desDistance = desSpeed * desTime;
		 */
	}

	public void calculate(double fl, double cs) throws Exception
	{
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
			ete += wp.setEte(cs);

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

	public Waypoint getArrival() throws NoSuchWaypointException
	{
		Waypoint wp = getLast();
		if (! wp.getType().equals(WaypointType.ICAO))
			throw new NoSuchWaypointException("Arrival not specified.");

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
