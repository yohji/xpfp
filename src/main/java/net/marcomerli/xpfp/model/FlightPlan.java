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

import net.marcomerli.xpfp.error.FlightPlanException;
import net.marcomerli.xpfp.error.GeoException;
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
	private boolean valid;

	public FlightPlan(String name) {

		this.name = name;
		filename = name.replaceAll("\\W+", "_");
	}

	public void calculate(final double crzAlt, final double crzSpeed,
		double clbRate, double clbSpeed, double desRate, double desSpeed)
		throws FlightPlanException, GeoException
	{
		for (Iterator<Waypoint> it = iterator(); it.hasNext();)
			if (it.next().isCalculated())
				it.remove();

		validate();

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

		validateVNav();
	}

	public void calculate(final double crzAlt, final double crzSpeed)
		throws FlightPlanException, GeoException
	{
		for (Iterator<Waypoint> it = iterator(); it.hasNext();)
			if (it.next().isCalculated())
				it.remove();

		validate();

		Waypoint dep = getDeparture();
		Location depLoc = dep.getLocation();
		GeoFn.elevation(depLoc);

		distance = 0.0;
		ete = 0L;

		for (int iWp = 1; iWp < size(); iWp++) {
			Waypoint prev = get(iWp - 1);
			Waypoint wp = get(iWp);

			Double dist = wp.setDistance(prev);
			wp.setCourse(prev);
			wp.getLocation().alt = crzAlt;
			ete += wp.setEte(crzSpeed);
			distance += dist;
		}

		Waypoint arr = getArrival();
		Location arrLoc = arr.getLocation();
		GeoFn.elevation(arrLoc);
	}

	public void validate() throws FlightPlanException
	{
		try {
			if (size() <= 1)
				throw new FlightPlanException(
					"Insufficient number of waypoints specified.");

			Waypoint wp = getDeparture();
			if (! wp.getType().equals(WaypointType.ICAO))
				throw new FlightPlanException("Departure not specified.");

			wp = getArrival();
			if (! wp.getType().equals(WaypointType.ICAO))
				throw new FlightPlanException("Arrival not specified.");

			valid = true;
		}
		catch (FlightPlanException e) {
			valid = false;
			throw e;
		}
	}

	public void validateVNav() throws FlightPlanException
	{
		try {
			Waypoint asc = null;
			Waypoint desc = null;
			for (int iWp = 0; iWp < size(); iWp++) {
				Waypoint wp = get(iWp);

				if (wp.getType().equals(WaypointType.DSC)) {
					desc = wp;

					if (asc == null)
						throw new FlightPlanException("Descent waypoint is before the ascent waypoint.");
				}
				else if (wp.getType().equals(WaypointType.ASC))
					asc = wp;
			}

			if (asc != null && desc == null)
				throw new FlightPlanException("Descent waypoint is out of the plan.");
			else if (asc == null && desc != null)
				throw new FlightPlanException("Ascent waypoint is out of the plan.");
			else if (asc == null && desc == null)
				throw new FlightPlanException("Ascent and Descent waypoints are out of the plan.");
		}
		catch (FlightPlanException e) {
			valid = false;
			throw e;
		}
	}

	public Waypoint getDeparture()
	{
		return getFirst();
	}

	public Waypoint getArrival()
	{
		return getLast();
	}

	public boolean isValid()
	{
		return valid;
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
		final double clbRate, final double clbSpeed, Location depLoc) throws GeoException
	{
		boolean clbWpAdd = false;
		double lastDistance = 0;

		double clbAlt = crzAlt - depLoc.alt;
		double clbDistance = (clbAlt / clbRate) * clbSpeed;

		for (int iWp = 1; iWp < size(); iWp++) {
			Waypoint prev = get(iWp - 1);
			Waypoint curr = get(iWp);

			curr.getLocation().alt = crzAlt;
			curr.setCourse(prev);
			double currDist = curr.setDistance(prev);
			curr.setEte(crzSpeed);

			double sumDistance = (lastDistance + currDist);
			if (! clbWpAdd && sumDistance > clbDistance) {

				Waypoint clbWp = new Waypoint();
				clbWp.setIdentifier(WaypointType.ASC.getFplCode());
				clbWp.setCalculated(true);
				clbWp.setType(WaypointType.ASC);
				clbWp.setLocation(GeoFn.point(prev.getLocation(),
					(clbDistance - lastDistance), curr.getBearing()));

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
			else
				lastDistance += currDist;
		}
	}

	private void calculateDescent(final double crzAlt, final double crzSpeed,
		final double desRate, final double desSpeed, Location arrLoc) throws GeoException
	{
		double lastDistance = 0;

		double desAlt = crzAlt - arrLoc.alt;
		double desDistance = (desAlt / desRate) * desSpeed;

		for (int iWp = (size() - 2); iWp > 0; iWp--) {
			int iWpNext = iWp + 1;
			Waypoint next = get(iWpNext);
			Waypoint curr = get(iWp);

			double currDist = next.getDistance();
			double sumDistance = (lastDistance + currDist);

			if (sumDistance > desDistance) {

				Waypoint desWp = new Waypoint();
				desWp.setIdentifier(WaypointType.DSC.getFplCode());
				desWp.setCalculated(true);
				desWp.setType(WaypointType.DSC);
				desWp.setLocation(GeoFn.point(next.getLocation(), (desDistance - lastDistance),
					GeoFn.bearing(next.getLocation(), curr.getLocation())));

				desWp.getLocation().alt = crzAlt;
				desWp.setCourse(curr);
				desWp.setDistance(curr);
				desWp.setEte(crzSpeed);

				next.setDistance(desWp);
				next.setEte(desSpeed);

				add(iWpNext, desWp);

				sumDistance = 0;
				for (int iFix = (size() - 2); iFix > iWpNext; iFix--) {
					Waypoint fixWp = get(iFix);
					sumDistance += get(iFix + 1).getDistance();

					fixWp.getLocation().alt = ((sumDistance * desRate) / desSpeed) + arrLoc.alt;
					fixWp.setEte(desSpeed);
				}

				iWp -= 1;
				break;
			}
			else
				lastDistance += currDist;
		}
	}
}
