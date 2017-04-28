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

/**
 * @author Marco Merli
 * @since 1.0
 */
public enum WaypointType {

	ICAO("AIRPORT", 1),
	VOR("VOR", 3),
	NDB("NDB", 2),
	INT("INT", 11),
	POS("USER WAYPOINT", 28);

	public static WaypointType get(String fplCode)
	{
		for (WaypointType t : values())
			if (t.fplCode.equals(fplCode))
				return t;

		throw new IllegalArgumentException("No such FPL type: " + fplCode);
	}

	public static WaypointType get(int fmsCode)
	{
		for (WaypointType t : values())
			if (t.fmsCode == fmsCode)
				return t;

		throw new IllegalArgumentException("No such FMS type: " + fmsCode);
	}

	private String fplCode;
	private int fmsCode;

	private WaypointType(String fplCode, int fmsCode) {

		this.fplCode = fplCode;
		this.fmsCode = fmsCode;
	}

	public String getFplCode()
	{
		return fplCode;
	}

	public int getFmsCode()
	{
		return fmsCode;
	}
}
