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

package net.marcomerli.xpfp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Constructor;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.error.DataException;
import net.marcomerli.xpfp.error.NetworkException;
import net.marcomerli.xpfp.error.ReaderException;
import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.file.read.Reader;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.fn.NetworkFn;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = - 2425410924278164802L;
	private static final Logger logger = LoggerFactory.getLogger(MenuBar.class);

	private MainWindow win;
	private JFileChooser fcImport;

	public MenuBar(MainWindow win) {

		this.win = win;

		fcImport = new JFileChooser();
		fcImport.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fcImport.setCurrentDirectory(Context.getPreferences()
			.getProperty(Preferences.DIR_IMPORT, File.class));
	}

	public JMenuBar init()
	{
		// File
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		JMenuItem menuItem = new JMenuItem("Import file", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(new OnImport(menuItem));
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Settings", KeyEvent.VK_COMMA);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_COMMA, ActionEvent.CTRL_MASK));
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

	private class OnImport implements ActionListener {

		private JMenuItem menuItem;

		public OnImport(JMenuItem menuItem) {

			this.menuItem = menuItem;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int returnVal = fcImport.showOpenDialog(menuItem);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fcImport.getSelectedFile();
				try {
					// FIXME: repaint window with spinner in the right moment
					win.setContentPane(new SpinnerContent());
					win.pack();
					win.validate();

					Preferences preferences = Context.getPreferences();
					preferences.setProperty(Preferences.DIR_IMPORT,
						fcImport.getCurrentDirectory().getAbsolutePath());
					preferences.save();

					NetworkFn.requireInternet();

					FileType type = FileType.get(file);
					Constructor<? extends Reader> constr = type.getReader().getConstructor(File.class);
					Reader reader = constr.newInstance(file);
					FlightPlan flightPlan = reader.read();
					Context.setFlightPlan(flightPlan);

					win.setContentPane(new MainContent(win));
					win.pack();
					win.validate();
				}
				catch (DataException ee) {
					GuiFn.warnDialog(ee, win);
				}
				catch (ReaderException | NetworkException ee) {
					GuiFn.errorDialog(ee, win);

					win.setContentPane(new HomeContent());
					win.setSize(Window.DEFAULT_WIN_SIZE);
					win.validate();
				}
				catch (Exception ee) {
					logger.error("onImport", ee);
					GuiFn.errorDialog(ee, win);

					win.setContentPane(new HomeContent());
					win.setSize(Window.DEFAULT_WIN_SIZE);
					win.validate();
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
