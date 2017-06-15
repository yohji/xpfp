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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class ValidateFormAction implements ActionListener {

	private JComponent[] fields;

	public ValidateFormAction(JComponent... fields) {

		this.fields = fields;
	}

	@Override
	public final void actionPerformed(ActionEvent e)
	{
		boolean allValid = true;

		if (fields != null && fields.length > 0) {
			for (JComponent comp : fields) {
				if (comp.isEnabled()) {

					InputVerifier verifier = comp.getInputVerifier();
					if (verifier != null) {

						boolean isValid = verifier.verify(comp);
						comp.setBorder(BorderFactory.createLineBorder(
							(isValid ? Color.gray : Color.red)));
						allValid &= isValid;
					}
				}
				else
					comp.setBorder(BorderFactory.createLineBorder(Color.gray));
			}
		}

		if (allValid)
			perform(e);
	}

	public abstract void perform(ActionEvent e);
}
