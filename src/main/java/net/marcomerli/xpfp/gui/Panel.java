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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import net.marcomerli.xpfp.gui.Window.EditMenuMouseListener;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Panel extends JPanel {

	private static final long serialVersionUID = - 7280965664177289899L;
	protected final Logger logger = Logger.getLogger(getClass());

	protected abstract class FormValidator implements ActionListener {

		private JComponent[] fields;

		public FormValidator(JComponent... fields) {

			this.fields = fields;
		}

		@Override
		public final void actionPerformed(ActionEvent e)
		{
			boolean allValid = true;

			if (fields != null && fields.length > 0) {
				for (JComponent comp : fields) {

					InputVerifier verifier = comp.getInputVerifier();
					if (verifier != null) {

						boolean isValid = verifier.verify(comp);
						comp.setBorder(BorderFactory.createLineBorder(
							(isValid ? Color.gray : Color.red)));
						allValid &= isValid;
					}
				}
			}

			if (allValid)
				perform(e);
		}

		public abstract void perform(ActionEvent e);
	}

	protected class TextInput extends JTextField {

		private static final long serialVersionUID = 3513321511037094181L;

		public TextInput() {

			addMouseListener(new EditMenuMouseListener(this));

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

	protected class NumberInput extends TextInput {

		private static final long serialVersionUID = - 3400518930083189803L;

		public NumberInput(final int maxSize) {

			addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e)
				{
					String text = NumberInput.this.getText();
					char key = e.getKeyChar();
					System.out.println((int) key);

					if (text.isEmpty() || key <= 31 || key == 127 || key == 65535)
						return;

					if ((key < 48 || key > 57) || text.length() > maxSize)
						setText(text.substring(0, text.length() - 1));
				}
			});
		}
	}

	protected class ValueLabel extends JLabel {

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
}
