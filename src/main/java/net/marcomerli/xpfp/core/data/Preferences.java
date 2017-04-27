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

package net.marcomerli.xpfp.core.data;

import java.io.File;

import net.marcomerli.xpfp.core.Context;


/**
 * @author Marco Merli
 * @since 1.0
 */
public class Preferences extends Data {

	private static final long serialVersionUID = 511605731858765879L;

	public static final String IMPORT_DIRECTORY = "import.directory";
	public static final String FP_FLIGHT_LEVEL = "fp.flight.level";
	public static final String FP_CRUISING_SPEED = "fp.cruising.speed";
	public static final String FP_VERTICAL_SPEED = "fp.vertical.speed";

	@Override
	protected File file()
	{
		return new File(Context.getHomedir(), "preferences.properties");
	}

	@Override
	protected void init()
	{}
}
