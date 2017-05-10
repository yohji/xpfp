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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.Version;
import net.marcomerli.xpfp.gui.Components.LinkLabel;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class AboutWindow extends Window {

	private static final long serialVersionUID = 5454273820569518074L;

	public static URL GITHUB_URL = null;
	static {
		try {
			GITHUB_URL = new URL("https://github.com/yohji/xpfp/");
		}
		catch (Exception e) {
			LoggerFactory.getLogger(AboutWindow.class).error("url", e);
		}
	}

	public AboutWindow() {

		super(TITLE_COMPACT + " :: About");

		Container mainPane = getContentPane();
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Information", information());
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("License", license());
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		mainPane.add(tabbedPane);
		setContentPane(mainPane);
		setSize(475, 475);

		setResizable(false);
		setLocationByPlatform(true);
		setVisible(true);
	}

	private JPanel information()
	{
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		pane.add(Box.createRigidArea(new Dimension(0, 50)));
		pane.add(new JLabel(TITLE_FULL));
		pane.add(new JLabel("Version: " + Version.get()));
		add(Box.createGlue());

		pane.add(Box.createRigidArea(new Dimension(0, 30)));
		pane.add(new JLabel("Copyright (c) 2017"));
		pane.add(new JLabel("Marco Merli <yohji@marcomerli.net>"));
		pane.add(new JLabel("All rights reserved."));
		pane.add(new JLabel("X-Plane is a registered trademark of Laminar Research."));
		pane.add(new LinkLabel("Follow Me on GitHub", GITHUB_URL));
		add(Box.createGlue());

		pane.add(Box.createRigidArea(new Dimension(0, 30)));
		pane.add(new JLabel("IT IS ONLY FOR FLIGHT SIMULATION!"));
		pane.add(new JLabel("DO NOT USE IT FOR REAL FLIGHT!"));
		pane.add(Box.createGlue());

		for (Component comp : pane.getComponents())
			((JComponent) comp).setAlignmentX(Component.CENTER_ALIGNMENT);

		return pane;
	}

	private JPanel license()
	{
		JPanel pane = new JPanel(new GridBagLayout());

		JTextArea textArea = new JTextArea(5, 20);
		textArea.setEditable(false);

		try (BufferedReader in = new BufferedReader(new InputStreamReader(
			getClass().getClassLoader().getResourceAsStream("LICENSE")))) {

			StringBuffer buffer = new StringBuffer();
			while (in.ready()) {
				buffer.append(in.readLine());
				buffer.append('\n');
			}

			textArea.setText(buffer.toString());
		}
		catch (Exception e) {
			logger.error("license", e);
		}

		textArea.setCaretPosition(0);
		JScrollPane scrollPane = new JScrollPane(textArea);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		pane.add(scrollPane, c);

		return pane;
	}
}
