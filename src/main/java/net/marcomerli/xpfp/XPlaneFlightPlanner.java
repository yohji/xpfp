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

import javax.swing.SwingUtilities;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.Settings;
import net.marcomerli.xpfp.fn.GeoApiFn;
import net.marcomerli.xpfp.gui.MainWindow;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class XPlaneFlightPlanner {

	public static void main(String[] args) throws Exception
	{
		Context.setSettings(new Settings().load());
		GeoApiFn.init();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				new MainWindow();
			}
		});
	}
}
