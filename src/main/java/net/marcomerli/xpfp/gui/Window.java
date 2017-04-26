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

import java.awt.HeadlessException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Window extends JFrame {

	private static final long serialVersionUID = 7604558040904896370L;
	protected final Logger logger = Logger.getLogger(getClass());

	public static final String TITLE_FULL = "X-Plane Flight Planner";
	public static final String TITLE_COMPACT = "XPFP";

	public Window(String title) throws HeadlessException {

		super(title);

		URL res = getClass().getClassLoader().getResource("xpfp.jpg");
		setIconImage(new ImageIcon(res).getImage());
	}
}
