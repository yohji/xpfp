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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.Settings;
import net.marcomerli.xpfp.fn.GuiFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class SettingsWindow extends JFrame {

	private static final long serialVersionUID = 5454273820569518074L;
	private static final Logger logger = Logger.getLogger(SettingsWindow.class);

	private JTextField fmsDirText;
	private JButton fmsDirBtn;
	private JFileChooser fmsDirFileChooser;
	private JTextField proxyHostnameText;
	private JTextField proxyPortText;
	private JCheckBox proxyActive;

	public SettingsWindow() {

		super(MainWindow.TITLE_COMPACT + " :: Settings");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		fmsDirFileChooser = new JFileChooser();
		fmsDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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
		mainPane.add(fmsDirPanel());
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(proxyPanel());
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(savePanel);
		mainPane.add(Box.createGlue());

		setContentPane(mainPane);
		pack();

		setMinimumSize(getMinimumSize());
		setLocationByPlatform(true);
		setVisible(true);
	}

	private JPanel fmsDirPanel()
	{
		JPanel fmsDirPanel = new JPanel(new BorderLayout());
		fmsDirPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("FMS Directory"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(fmsDirPanel);

		fmsDirText = new JTextField();
		fmsDirText.setEnabled(false);
		fmsDirText.setText(Context.getSettings()
			.getFMSDirectory().getAbsolutePath());

		fmsDirBtn = new JButton("Choose");
		fmsDirBtn.addActionListener(new OnChooseDir());

		layout.row().grid().add(fmsDirText, 2).add(fmsDirBtn);

		return fmsDirPanel;
	}

	private JPanel proxyPanel()
	{
		JPanel proxyPanel = new JPanel(new BorderLayout());
		proxyPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Proxy"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(proxyPanel);
		Settings settings = Context.getSettings();

		proxyActive = new JCheckBox();
		proxyActive.setSelected(settings.isProxyActive());
		layout.row().grid(new JLabel("Active", JLabel.TRAILING)).add(proxyActive);

		proxyHostnameText = new JTextField();
		proxyHostnameText.setText(settings.getProxyHostname());
		layout.row().grid(new JLabel("Hostname", JLabel.TRAILING)).add(proxyHostnameText);

		proxyPortText = new JTextField();
		proxyPortText.setText(settings.getProxyPort().toString());
		layout.row().grid(new JLabel("Port", JLabel.TRAILING)).add(proxyPortText);

		return proxyPanel;
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
			try {
				Settings settings = Context.getSettings();
				settings.setFMSDirectory(fmsDirText.getText());
				settings.setProxyActive(proxyActive.isSelected());
				settings.setProxyHostname(proxyHostnameText.getText());
				settings.setProxyPort(proxyPortText.getText());

				settings.save();
			}
			catch (Exception ee) {
				logger.error("onSave", ee);
				GuiFn.errorPopup(ee, SettingsWindow.this);
			}
			finally {
				dispose();
			}
		}
	}
}
