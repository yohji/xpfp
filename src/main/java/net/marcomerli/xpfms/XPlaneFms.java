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

package net.marcomerli.xpfms;

import javax.swing.SwingUtilities;

import net.marcomerli.xpfms.gui.Gui;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class XPlaneFms {

	public static void main(String[] args) throws Exception
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				final Gui gui = new Gui();
				gui.setVisible(true);
			}
		});
	}
}
