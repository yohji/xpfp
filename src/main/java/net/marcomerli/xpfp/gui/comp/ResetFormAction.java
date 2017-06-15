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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class ResetFormAction implements ActionListener {

	private JComponent[] fields;

	public ResetFormAction(JComponent... fields) {

		this.fields = fields;
	}

	@Override
	public final void actionPerformed(ActionEvent e)
	{
		if (fields != null && fields.length > 0) {
			for (JComponent comp : fields) {

				if (JTextComponent.class.isAssignableFrom(comp.getClass())) {
					((JTextComponent) comp).setText("");
				}
				else if (JToggleButton.class.isAssignableFrom(comp.getClass())) {
					((JToggleButton) comp).setSelected(false);
				}
				else if (JSlider.class.isAssignableFrom(comp.getClass())) {
					JSlider slider = (JSlider) comp;
					slider.setValue(slider.getModel().getMinimum());
				}
			}
		}

		perform(e);
	}

	public void perform(ActionEvent e)
	{}
}
