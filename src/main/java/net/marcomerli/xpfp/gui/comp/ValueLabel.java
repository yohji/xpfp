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

import javax.swing.JLabel;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class ValueLabel extends JLabel {

	private static final long serialVersionUID = - 8172651693355521184L;

	private String label;

	public ValueLabel(String label, Object value) {

		this.label = label;
		setValue(value);
	}

	public void setValue(Object value)
	{
		setText(String.format("%s: %s",
			label, value));
	}
}
