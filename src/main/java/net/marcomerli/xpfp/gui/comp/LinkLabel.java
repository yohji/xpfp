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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.fn.GuiFn;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class LinkLabel extends JButton {

	private static final long serialVersionUID = - 7546019315359390845L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public LinkLabel(final String label, final URL url) {

		super(label);

		setBorder(BorderFactory.createEmptyBorder());
		setBorderPainted(false);
		setOpaque(false);
		setBackground(Color.WHITE);

		if (GuiFn.isBrowserSupported()) {

			setForeground(Color.BLUE);
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setToolTipText(url.toString());
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					try {
						GuiFn.openBrowser(url);
					}
					catch (Exception ee) {
						logger.error("openBrowser", ee);
					}
				}
			});
		}
	}

	public LinkLabel(final String label, final String url) throws MalformedURLException {

		this(label, new URL(url));
	}
}
