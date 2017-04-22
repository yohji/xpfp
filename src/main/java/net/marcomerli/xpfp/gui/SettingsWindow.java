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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class SettingsWindow extends JFrame {

	private static final long serialVersionUID = 5454273820569518074L;

	private JTextField fmsDirText;
	private JButton fmsDirBtn;
	private JFileChooser fmsDirFileChooser;

	public SettingsWindow() {

		super(MainWindow.TITLE + " :: Settings");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// FMS Directory
		JPanel fmsDirPanel = new JPanel(new BorderLayout());
		fmsDirPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("FMS Directory"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		fmsDirText = new JTextField();
		fmsDirText.setEnabled(false);
		fmsDirPanel.add(fmsDirText);

		fmsDirBtn = new JButton("Choose");
		fmsDirBtn.addActionListener(new OnChooseDir());
		fmsDirPanel.add(fmsDirBtn);

		fmsDirFileChooser = new JFileChooser();
		fmsDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Proxy
		JPanel proxyPanel = new JPanel(new BorderLayout());
		proxyPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Proxy"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Save
		JPanel savePanel = new JPanel();
		JButton save = new JButton("Save");
		save.addActionListener(new OnSave());
		savePanel.add(save);

		// Main
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));

		mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(fmsDirPanel);
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(proxyPanel);
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(savePanel);
		mainPane.add(Box.createGlue());

		setContentPane(mainPane);
		// pack();
		// setPreferredSize(new Dimension(450, 250));
		setSize(450, 260);

		setLocationByPlatform(true);
		setVisible(true);
	}

	private class OnChooseDir implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int returnVal = fmsDirFileChooser.showOpenDialog(fmsDirBtn);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File fpl = fmsDirFileChooser.getSelectedFile();
				fmsDirText.setText(fpl.getAbsolutePath());
			}
		}
	}

	private class OnSave implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO: save settings

			dispose();
		}
	}
}
