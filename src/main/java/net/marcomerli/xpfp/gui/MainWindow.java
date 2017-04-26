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

package net.marcomerli.xpfp.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import net.marcomerli.xpfp.core.Context;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 109257433124133997L;

	public static final String TITLE_FULL = "X-Plane Flight Planner";
	public static final String TITLE_COMPACT = "XPFP";

	public MainWindow() {

		super(TITLE_FULL);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(new MenuBar(this).init());
		setContentPane(new Home());

		setSize(400, 200);
		setLocationByPlatform(true);
		setResizable(false);
		setVisible(true);
	}

	private class Home extends JPanel {

		private static final long serialVersionUID = - 1307131782278478525L;

		public Home() {

			new BoxLayout(this, BoxLayout.PAGE_AXIS);
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			add(Box.createRigidArea(new Dimension(0, 100)));
			add(new JLabel("No flight plan; import one from the menu File -> Import..."));
			add(Box.createGlue());

			if (StringUtils.isBlank(Context.getSettings().getGeoApiKey()))
				new SettingsWindow();
		}
	}
}
