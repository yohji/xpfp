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

package net.marcomerli.xpfp;

// TODO: writing the README
// TODO: logging into file
// TODO: importing FMS files
// TODO: information tab in about
// TODO: improving home page
// TODO: exporting in GPX, KML and GML file format
// TODO: flight plan elevation profile graph

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.fn.GeoFn;
import net.marcomerli.xpfp.gui.MainWindow;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class XPlaneFlightPlanner {

	protected static final Logger logger = Logger.getLogger(XPlaneFlightPlanner.class);

	public static void main(String[] args) throws Exception
	{
		try {
			Context.init();

			Settings settings = new Settings();
			settings.load();
			Context.setSettings(settings);

			GeoFn.init();

			Preferences prefs = new Preferences();
			prefs.load();
			Context.setPreferences(prefs);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					new MainWindow();
				}
			});
		}
		catch (Exception e) {
			logger.error("main", e);
		}
	}
}
