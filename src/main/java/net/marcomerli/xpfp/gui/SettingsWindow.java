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

import net.java.dev.designgridlayout.DesignGridLayout;
import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.fn.GeoFn;
import net.marcomerli.xpfp.fn.GuiFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class SettingsWindow extends Window {

	private static final long serialVersionUID = 5454273820569518074L;

	private JTextField fmsDirText;
	private JButton fmsDirBtn;
	private JFileChooser fmsDirFileChooser;
	private JTextField geoApiText;
	private JTextField proxyHostnameText;
	private JTextField proxyPortText;
	private JCheckBox proxyActive;
	private JCheckBox proxyAuth;
	private JTextField proxyAuthUsername;
	private JTextField proxyAuthPassword;

	public SettingsWindow() {

		super(TITLE_COMPACT + " :: Settings");

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
		mainPane.add(geoApiKey());
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(proxyPanel());
		mainPane.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPane.add(savePanel);
		mainPane.add(Box.createGlue());

		setContentPane(mainPane);
		setSize(350, 450);

		setResizable(false);
		setLocationByPlatform(true);
		setVisible(true);
	}

	private JPanel fmsDirPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("FMS Directory"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(panel);

		fmsDirText = new JTextField();
		fmsDirText.setEnabled(false);
		fmsDirText.setText(Context.getSettings()
			.getProperty(Settings.EXPORT_DIRECTORY, File.class).getAbsolutePath());

		fmsDirBtn = new JButton("Choose");
		fmsDirBtn.addActionListener(new OnChooseDir());

		layout.row().grid().add(fmsDirText, 2).add(fmsDirBtn);

		return panel;
	}

	private JPanel geoApiKey()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Google GeoApi"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		geoApiText = new JTextField();
		geoApiText.setText(Context.getSettings().getProperty(Settings.GEOAPI_KEY));

		DesignGridLayout layout = new DesignGridLayout(panel);
		layout.row().grid(new JLabel("Key", JLabel.TRAILING)).add(geoApiText);

		return panel;
	}

	private JPanel proxyPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Proxy"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(panel);
		Settings settings = Context.getSettings();

		proxyActive = new JCheckBox();
		proxyActive.setSelected(settings.getProperty(Settings.PROXY_ACTIVE, Boolean.class));
		layout.row().grid(new JLabel("Active", JLabel.TRAILING)).add(proxyActive);

		proxyHostnameText = new JTextField();
		proxyHostnameText.setText(settings.getProperty(Settings.PROXY_HOSTNAME));
		layout.row().grid(new JLabel("Hostname", JLabel.TRAILING)).add(proxyHostnameText);

		proxyPortText = new JTextField();
		proxyPortText.setText(settings.getProperty(Settings.PROXY_PORT));
		layout.row().grid(new JLabel("Port", JLabel.TRAILING)).add(proxyPortText);
		
		proxyAuth = new JCheckBox();
		proxyAuth.setSelected(settings.getProperty(Settings.PROXY_AUTH, Boolean.class));
		layout.row().grid(new JLabel("Authentication", JLabel.TRAILING)).add(proxyAuth);
		
		proxyAuthUsername = new JTextField();
		proxyAuthUsername.setText(settings.getProperty(Settings.PROXY_AUTH_USERNAME));
		layout.row().grid(new JLabel("Username", JLabel.TRAILING)).add(proxyAuthUsername);
		
		proxyAuthPassword = new JTextField();
		proxyAuthPassword.setText(settings.getProperty(Settings.PROXY_AUTH_PASSWORD));
		layout.row().grid(new JLabel("Password", JLabel.TRAILING)).add(proxyAuthPassword);

		return panel;
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
				settings.setProperty(Settings.EXPORT_DIRECTORY, fmsDirText.getText());
				settings.setProperty(Settings.GEOAPI_KEY, geoApiText.getText());
				settings.setProperty(Settings.PROXY_ACTIVE, String.valueOf(proxyActive.isSelected()));
				settings.setProperty(Settings.PROXY_HOSTNAME, proxyHostnameText.getText());
				settings.setProperty(Settings.PROXY_PORT, proxyPortText.getText());
				settings.setProperty(Settings.PROXY_AUTH, String.valueOf(proxyAuth.isSelected()));
				settings.setProperty(Settings.PROXY_AUTH_USERNAME, proxyAuthUsername.getText());
				settings.setProperty(Settings.PROXY_AUTH_PASSWORD, proxyAuthPassword.getText());

				settings.save();
				Context.refresh();
				GeoFn.init();
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
