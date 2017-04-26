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

import org.apache.log4j.Logger;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.file.read.FPLReader;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = - 2425410924278164802L;
	private static final Logger logger = Logger.getLogger(MenuBar.class);

	private MainWindow win;
	private JFileChooser fcFPL;

	public MenuBar(MainWindow win) {

		this.win = win;

		fcFPL = new JFileChooser();
		fcFPL.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fcFPL.setCurrentDirectory(Context.getPreferences()
			.getProperty(Preferences.IMPORT_DIRECTORY, File.class));
	}

	public JMenuBar init()
	{
		// File
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		JMenuItem menuItem = new JMenuItem("Import Garmin FPL", KeyEvent.VK_G);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_G, ActionEvent.CTRL_MASK));
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

		// Help
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		add(menu);

		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new OnAbout());
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

				Preferences preferences = Context.getPreferences();
				preferences.setProperty(Preferences.IMPORT_DIRECTORY,
					fcFPL.getCurrentDirectory().getAbsolutePath());
				preferences.save();

				try {
					FPLReader fplReader = new FPLReader(fpl);
					FlightPlan flightPlan = fplReader.read();

					Context.setFlightPlan(flightPlan);
					win.setContentPane(new MainContent(win));
					win.pack();
					win.validate();
				}
				catch (Exception ee) {
					logger.error("onImport", ee);
					GuiFn.errorPopup(ee, win);
				}
			}
		}
	}

	private class OnSettings implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			new SettingsWindow();
		}
	}

	private class OnExit implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}

	public class OnAbout implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			new AboutWindow();
		}
	}
}
