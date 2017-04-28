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

package net.marcomerli.xpfp.file.write;

import java.io.File;
import java.io.FileWriter;

import net.marcomerli.xpfp.fn.NumberFn;
import net.marcomerli.xpfp.fn.UnitFn;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FMSWriter {

	private static final String HEADER = String.format("I%n3 version%n1%n");

	private File file;

	public FMSWriter(File file) {

		this.file = file;
	}

	public void write(FlightPlan flightPlan) throws Exception
	{
		try (FileWriter writer = new FileWriter(file)) {

			writer.write(HEADER);
			writer.write(String.format("%d%n", flightPlan.size()));

			for (Waypoint wp : flightPlan) {
				Location loc = wp.getLocation();

				writer.write(String.format("%d %s %d %s %s%n",
					wp.getType().getFmsCode(), wp.getFMSIdentifier(),
					(long) UnitFn.mToFt(loc.alt),
					NumberFn.format(loc.lat, 6),
					NumberFn.format(loc.lng, 6)));
			}
		}
	}
}
