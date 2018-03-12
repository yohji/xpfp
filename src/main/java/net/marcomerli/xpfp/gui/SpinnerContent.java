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

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class SpinnerContent extends JPanel {

	private static final long serialVersionUID = 1115688377888163553L;

	public SpinnerContent() {

		new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setBorder(BorderFactory.createEmptyBorder(75, 75, 75, 75));

		ClassLoader cldr = this.getClass().getClassLoader();
		URL gif = cldr.getResource("spinner.gif");
		ImageIcon icon = new ImageIcon(gif);

		JLabel label = new JLabel();
		label.setIcon(icon);
		icon.setImageObserver(label);

		add(label);
	}
}
