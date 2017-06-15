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

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class TextInput extends JTextField {

	private static final long serialVersionUID = 3513321511037094181L;

	public TextInput() {

		addMouseListener(new TextPopup(this));

		setVerifyInputWhenFocusTarget(false);
		setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input)
			{
				String text = ((JTextField) input).getText();
				return StringUtils.isNoneEmpty(text);
			}
		});
	}
}
