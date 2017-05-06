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

	public void calculate(final double crzAlt, final double crzSpeed,
		double clbRate, double clbSpeed,
		double desRate, double desSpeed)
		throws Exception
	{
		if (size() <= 1)
			return;

		for (Iterator<Waypoint> it = iterator(); it.hasNext();)
			if (it.next().isCalculated())
				it.remove();

		Waypoint dep = getDeparture();
		Location depLoc = dep.getLocation();
		GeoFn.elevation(depLoc);

		calculateClimb(crzAlt, crzSpeed, clbRate, clbSpeed, depLoc);

		Waypoint arr = getArrival();
		Location arrLoc = arr.getLocation();
		GeoFn.elevation(arrLoc);

		calculateDescent(crzAlt, crzSpeed, desRate, desSpeed, arrLoc);

		distance = 0.0;
		ete = 0L;

		for (int iWp = 1; iWp < size(); iWp++) {
			Waypoint wp = get(iWp);

			distance += wp.getDistance();
			ete += wp.getEte();
		}
	}

	public void calculate(final double crzAlt, final double crzSpeed) throws Exception
	{
		if (size() <= 1)
			return;

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
			ete += wp.setEte(crzSpeed);

			loc = wp.getLocation();
			if (wp.getType().equals(WaypointType.ICAO))
				GeoFn.elevation(loc);
			else
				loc.alt = crzAlt;

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

	//
	// Internal
	//

	private void calculateClimb(final double crzAlt, final double crzSpeed,
		final double clbRate, final double clbSpeed, Location depLoc)
	{
		boolean clbWpAdd = false;
		double prevDistance = 0;

		double clbAlt = crzAlt - depLoc.alt;
		double clbDistance = (clbAlt / clbRate) * clbSpeed;

		for (int iWp = 1; iWp < size(); iWp++) {
			Waypoint prev = get(iWp - 1);
			Waypoint curr = get(iWp);

			curr.getLocation().alt = crzAlt;
			curr.setCourse(prev);
			double currDist = curr.setDistance(prev);
			curr.setEte(crzSpeed);

			double sumDistance = (prevDistance + currDist);
			if (! clbWpAdd && sumDistance > clbDistance) {

				Waypoint clbWp = new Waypoint();
				clbWp.setIdentifier("ʌʌʌ");
				clbWp.setCalculated(true);
				clbWp.setType(WaypointType.POS);
				clbWp.setLocation(GeoFn.point(prev.getLocation(),
					(clbDistance - prevDistance), curr.getBearing()));

				clbWp.getLocation().alt = crzAlt;
				clbWp.setCourse(prev);
				clbWp.setDistance(prev);
				clbWp.setEte(clbSpeed);

				curr.setDistance(clbWp);
				curr.setEte(crzSpeed);

				add(iWp, clbWp);

				sumDistance = 0;
				for (int iFix = 1; iFix < iWp; iFix++) {
					Waypoint fixWp = get(iFix);
					sumDistance += fixWp.getDistance();

					fixWp.getLocation().alt = ((sumDistance * clbRate) / clbSpeed) + depLoc.alt;
					fixWp.setEte(clbSpeed);
				}

				iWp += 1;
				clbWpAdd = true;
			}
			else {
				prevDistance += currDist;
			}
		}
	}

	private void calculateDescent(final double crzAlt, final double crzSpeed,
		final double desRate, final double desSpeed, Location arrLoc)
	{
		boolean desWpAdd = false;
		double prevDistance = 0;

		double desAlt = crzAlt - arrLoc.alt;
		double desDistance = (desAlt / desRate) * desSpeed;

		for (int iWp = (size() - 1); iWp > 0; iWp--) {
			Waypoint prev = get(iWp - 1);
			Waypoint curr = get(iWp);

			double currDist = curr.getDistance();
			double sumDistance = (prevDistance + currDist);

			if (! desWpAdd && sumDistance > desDistance) {

				Waypoint desWp = new Waypoint();
				desWp.setIdentifier("ṿṿṿ");
				desWp.setCalculated(true);
				desWp.setType(WaypointType.POS);
				desWp.setLocation(GeoFn.point(prev.getLocation(),
					(desDistance - prevDistance), curr.getBearing()));

				desWp.getLocation().alt = crzAlt;
				desWp.setCourse(prev);
				desWp.setDistance(prev);
				desWp.setEte(desSpeed);

				curr.setDistance(desWp);
				curr.setEte(crzSpeed);

				add(iWp, desWp);

				sumDistance = 0;
				for (int iFix = (size() - 2); iFix > iWp; iFix--) {
					Waypoint fixWp = get(iFix);
					sumDistance += fixWp.getDistance();

					fixWp.getLocation().alt = ((sumDistance * desRate) / desSpeed) + arrLoc.alt;
					fixWp.setEte(desSpeed);
				}

				iWp -= 1;
				desWpAdd = true;
			}
			else {
				prevDistance += currDist;
			}
		}
	}
}
