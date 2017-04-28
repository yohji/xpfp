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

package net.marcomerli.xpfp.core.data;

import java.io.File;

import net.marcomerli.xpfp.core.Context;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Preferences extends Data {

	private static final long serialVersionUID = 511605731858765879L;

	public static final String IMPORT_DIRECTORY = "directory.import.last";
	public static final String FP_FLIGHT_LEVEL = "flightlevel.fl";
	public static final String FP_CRUISING_SPEED = "flightlevel.cs";
	public static final String FP_ASC_RATE_oF_CLIMB = "flightlevel.asc.roc";
	public static final String FP_ASC_SPEED = "flightlevel.asc.speed";
	public static final String FP_DESC_RATE_oF_CLIMB = "flightlevel.desc.roc";
	public static final String FP_DESC_SPEED = "flightlevel.desc.speed";

	@Override
	protected File file()
	{
		return new File(Context.getHomedir(), "preferences.properties");
	}

	@Override
	protected void init()
	{}

	@Override
	protected void upgrade(String oldVersion)
	{}
}
