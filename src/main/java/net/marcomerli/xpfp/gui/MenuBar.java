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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.file.read.FPLReader;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = - 2425410924278164802L;

	private Gui gui;
	private JFileChooser fcFPL;

	public MenuBar(Gui guiFrame) {

		this.gui = guiFrame;

		fcFPL = new JFileChooser();
		fcFPL.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	}

	public JMenuBar init()
	{
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		JMenuItem menuItem = new JMenuItem("Import Garmin FPL", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new OnImportFPL(menuItem));
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Settings", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new OnSettings());
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new OnExit());
		menu.add(menuItem);

		return this;
	}

	private class OnImportFPL implements ActionListener {

		private JMenuItem menuItem;

		public OnImportFPL(JMenuItem menuItem) {

			this.menuItem = menuItem;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int returnVal = fcFPL.showOpenDialog(menuItem);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File fpl = fcFPL.getSelectedFile();
				FPLReader fplReader = new FPLReader(fpl);

				try {
					FlightPlan flightPlan = fplReader.read();
					Context.setFlightPlan(flightPlan);

					// TODO: load flight plan table without exporting yet
					File fms = new File(new File(System.getProperty("java.io.tmpdir")),
						flightPlan.getFilename());

					new FMSWriter(fms).write(flightPlan);
					GuiFn.infoPopup("Export completed", gui);
				}
				catch (Exception ee) {
					GuiFn.errorPopup(ee, gui);
				}
			}
		}
	}

	private class OnSettings implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			new Settings();
		}
	}

	private class OnExit implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			gui.dispose();
		}
	}
}
