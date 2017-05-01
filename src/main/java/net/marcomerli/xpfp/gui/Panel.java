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
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.gui.Window.EditMenuMouseListener;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Panel extends JPanel {

	private static final long serialVersionUID = - 7280965664177289899L;
	protected final Logger logger = Logger.getLogger(getClass());

	protected static abstract class ValidateFormAction implements ActionListener {

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

	protected static class ResetFormAction implements ActionListener {

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

	protected static class TextInput extends JTextField {

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

	protected static class NumberInput extends TextInput {

		private static final long serialVersionUID = - 3400518930083189803L;

		public NumberInput(final int maxSize) {

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

	protected static class LinkLabel extends JButton {

		private static final long serialVersionUID = - 7546019315359390845L;

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
							ee.printStackTrace();
						}
					}
				});
			}
		}

		public LinkLabel(final String label, final String url) throws MalformedURLException {

			this(label, new URL(url));
		}
	}

	protected static class ValueLabel extends JLabel {

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
