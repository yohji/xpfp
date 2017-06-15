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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class NumberInput extends TextInput {

	private static final long serialVersionUID = - 3400518930083189803L;

	public NumberInput(final int maxSize) {

		setColumns(maxSize);
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e)
			{
				String text = NumberInput.this.getText();
				char key = e.getKeyChar();

				if (text.isEmpty() || key <= 31 || key == 127 || key == 65535)
					return;

				if ((key < 48 || key > 57) || text.length() > maxSize)
					setText(text.substring(0, text.length() - 1));
			}
		});
	}
}
