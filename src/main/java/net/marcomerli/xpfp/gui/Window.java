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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Window extends JFrame {

	private static final long serialVersionUID = 7604558040904896370L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String TITLE_FULL = "X-Plane Flight Planner";
	public static final String TITLE_COMPACT = "XPFP";
	public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	public static final Dimension DEFAULT_WIN_SIZE = new Dimension(400, 200);

	public Window(String title) throws HeadlessException {

		super(title);

		URL res = getClass().getClassLoader().getResource("xpfp.png");
		setIconImage(new ImageIcon(res).getImage());
	}
}
