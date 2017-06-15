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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class EnableablePanel extends FormPanel {

	private static final long serialVersionUID = 1478891565610026831L;

	public EnableablePanel(String title, boolean enabled) {

		CheckBoxTitledBorder border = new CheckBoxTitledBorder(title, enabled);
		border.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setEnabled(((JCheckBox) e.getSource()).isSelected());
			}
		});

		setBorder(border);
		setEnabled(enabled);
	}

	public EnableablePanel(String title) {

		this(title, true);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);

		((CheckBoxTitledBorder) getBorder()).setSelected(enabled);

		for (Component comp : getComponents())
			if (! EnableablePanel.class.isAssignableFrom(comp.getClass()))
				comp.setEnabled(enabled);
	}
}
