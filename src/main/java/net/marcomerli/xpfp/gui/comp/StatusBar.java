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

package net.marcomerli.xpfp.gui.comp;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import net.marcomerli.xpfp.gui.Window;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = - 3400518930083189803L;

	private JLabel label;

	public StatusBar(Window win, String text) {

		setBorder(new BevelBorder(BevelBorder.RAISED));
		setPreferredSize(new Dimension(win.getWidth(), 16));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		add(label);
	}

	public StatusBar(Window win) {

		this(win, "");
	}

	public void setText(String text)
	{
		label.setText(text);
	}
}
