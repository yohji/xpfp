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

package net.marcomerli.xpfp.core;

import java.io.File;

import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Context {

	private static File homeDir;
	private static Settings settings;
	private static Preferences preferences;
	private static FlightPlan flightPlan;

	public static void init()
	{
		homeDir = new File(new File(System.getProperty("user.home")), ".xpfp");
		if (! homeDir.exists())
			if (! homeDir.mkdir())
				throw new IllegalStateException(
					"Failed to create home directory at " + homeDir.getAbsolutePath());
	}

	public static File getHomedir()
	{
		return homeDir;
	}

	public static Settings getSettings()
	{
		return settings;
	}

	public static void setSettings(Settings settings)
	{
		Context.settings = settings;
	}

	public static Preferences getPreferences()
	{
		return preferences;
	}

	public static void setPreferences(Preferences preferences)
	{
		Context.preferences = preferences;
	}

	public static FlightPlan getFlightPlan()
	{
		return flightPlan;
	}

	public static void setFlightPlan(FlightPlan flightPlan)
	{
		Context.flightPlan = flightPlan;
	}
}
