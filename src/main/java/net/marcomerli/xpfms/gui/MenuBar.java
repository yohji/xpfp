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

package net.marcomerli.xpfms.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = - 2425410924278164802L;

	private GuiFrame guiFrame;
	private JFileChooser importFPL;

	public MenuBar(GuiFrame guiFrame) {

		this.guiFrame = guiFrame;
		importFPL = new JFileChooser();
		importFPL.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	}

	public JMenuBar init()
	{
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		add(menu);

		JMenuItem menuItem = new JMenuItem("Import Garmin FPL", KeyEvent.VK_G);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_G, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ImportFPL(menuItem));
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Settings", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new Exit());
		menu.add(menuItem);

		return this;
	}

	private class ImportFPL implements ActionListener {

		private JMenuItem menuItem;

		public ImportFPL(JMenuItem menuItem) {

			this.menuItem = menuItem;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int returnVal = importFPL.showOpenDialog(menuItem);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File file = importFPL.getSelectedFile();
				JOptionPane.showMessageDialog(guiFrame, file.getAbsolutePath(),
					"X-Plane FMS", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class Exit implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			guiFrame.dispose();
		}
	}
}
