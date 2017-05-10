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

package net.marcomerli.xpfp.file.read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang3.StringUtils;

import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.fn.NumberFn;
import net.marcomerli.xpfp.fn.UnitFn;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FMSReader extends Reader {

	private static final String PADDING = "0 ---- 0.000000 0.000000 0.000000";

	public FMSReader(File file) {

		super(file, FileType.FMS);
	}

	@Override
	public FlightPlan read() throws Exception
	{
		FlightPlan flightPlan = new FlightPlan(file.getName().replaceFirst("[.][^.]+$", ""));

		try (BufferedReader in = new BufferedReader(new FileReader(file))) {

			Integer fpSize = null;
			for (int iLine = 1; in.ready(); iLine += 1) {
				String line = StringUtils.trimToEmpty(in.readLine());

				switch (iLine) {
				case 1:
					validate(line.matches("I|A"), "Invalid FMS file format.");
				break;
				case 2:
					validate(line.equals("3 version"), "Invalid FMS file format.");
				break;
				case 3:
					validate(line.matches("0|1"), "Invalid FMS file format.");
				break;
				case 4:
					validate(NumberFn.isNumeric(line), "Invalid FMS file format.");
					fpSize = Integer.valueOf(line);
				break;
				default:
					if (line.equals(PADDING))
						break;

					String[] parts = line.split(" ");
					validate(parts.length == 5, "Invalid FMS file format.");

					Waypoint waypoint = new Waypoint();
					waypoint.setType(WaypointType.get(Integer.valueOf(parts[0])));

					if (! waypoint.getType().equals(WaypointType.POS))
						waypoint.setIdentifier(parts[1]);
					else
						waypoint.setIdentifier("-");

					Double lat = Double.valueOf(parts[3]);
					Double lng = Double.valueOf(parts[4]);

					Location loc = new Location(lat, lng);
					loc.alt = UnitFn.ftToM(Double.valueOf(parts[2]));
					waypoint.setLocation(loc);

					flightPlan.add(waypoint);
				}
			}

			validate(fpSize.equals(flightPlan.size()), "Invalid FMS file format.");
		}

		return flightPlan;
	}
}
