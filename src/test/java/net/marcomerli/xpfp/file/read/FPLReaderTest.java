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

package net.marcomerli.xpfp.file.read;

import java.io.File;

import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.error.NoSuchWaypointException;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

public class FPLReaderTest extends UnitTestSupport {

	private static FlightPlan flightPlan;

	@Test
	public void sample01() throws Exception
	{
		_text("sample_01.fpl");
	}

	@Test
	public void sample02() throws Exception
	{
		_text("sample_02.fpl");
	}

	private void _text(String filename) throws Exception
	{
		File file = new File(this.getClass().getClassLoader().getResource(filename).getPath());
		flightPlan = new FPLReader(file).read();

		assertNotNull(flightPlan);
		assertStringNotBlank(flightPlan.getName());
		assertStringNotBlank(flightPlan.getFilename());

		try {
			flightPlan.getDeparture();
			flightPlan.getDestination();
		}
		catch (NoSuchWaypointException e) {
			failWhenExceptionNotExpected(e);
		}
		assertNotEmpty(flightPlan);
		for (Waypoint wp : flightPlan) {

			assertStringNotBlank(wp.getIdentifier());
			assertNotNull(wp.getType());
			if (! wp.getType().equals(WaypointType.POS))
				assertStringNotBlank(wp.getCountry());

			Location loc = wp.getLocation();

			assertNotNull(loc);
			assertNumberNotZero(loc.lat);
			assertNumberNotZero(loc.lng);
		}
	}

	public static FlightPlan getFlightPlan()
	{
		return flightPlan;
	}
}
