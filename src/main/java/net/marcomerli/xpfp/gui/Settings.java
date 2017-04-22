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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
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
public class Settings extends JFrame {

	private static final long serialVersionUID = 5454273820569518074L;

	private JTextField fmsDir;
	private JButton fmsDirBtn;
	private JFileChooser fmsDirFc;

	public Settings() {

		super(Gui.TITLE + " :: Settings");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// FMS Directory
		JPanel jFMSDir = new JPanel(new BorderLayout());
		jFMSDir.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("FMS Directory"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		fmsDir = new JTextField();
		fmsDir.setEnabled(false);
		jFMSDir.add(fmsDir);

		fmsDirBtn = new JButton("Choose");
		fmsDirBtn.addActionListener(new OnChooseDir());
		jFMSDir.add(fmsDirBtn);

		fmsDirFc = new JFileChooser();
		fmsDirFc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		mainPanel.add(jFMSDir, BorderLayout.CENTER);

		// Save
		JPanel jSave = new JPanel();
		JButton save = new JButton("Save");
		save.addActionListener(new OnSave());
		mainPanel.add(save);
		mainPanel.add(jSave, BorderLayout.CENTER);

		getContentPane().setLayout(new GridLayout());
		getContentPane().add(mainPanel);

		pack();
		setLocationByPlatform(true);
		setVisible(true);
	}

	private class OnChooseDir implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int returnVal = fmsDirFc.showOpenDialog(fmsDirBtn);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File fpl = fmsDirFc.getSelectedFile();
				fmsDir.setText(fpl.getAbsolutePath());
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
