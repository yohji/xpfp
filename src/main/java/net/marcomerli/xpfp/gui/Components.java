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
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.gui.Window.TextPopup;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Components {

	protected static final Logger logger = LoggerFactory.getLogger(Components.class);

	/**
	 * @author Philip Isenhour
	 * @author Marco Merli
	 */
	protected static class FormPanel extends JPanel {

		private static final long serialVersionUID = - 4777808857168558526L;
		private static final double DEFAULT_WEIGHT = 1.0;

		private final GridBagConstraints cLast;
		private final GridBagConstraints cMiddle;

		public FormPanel() {

			super(new GridBagLayout());

			cLast = new GridBagConstraints();
			cLast.fill = GridBagConstraints.HORIZONTAL;
			cLast.anchor = GridBagConstraints.NORTHWEST;
			cLast.insets = new Insets(5, 5, 5, 5);
			cLast.gridwidth = GridBagConstraints.REMAINDER;

			cMiddle = (GridBagConstraints) cLast.clone();
			cMiddle.gridwidth = 1;
		}

		public JLabel addLabel(String label)
		{
			return addLabel(label, DEFAULT_WEIGHT);
		}

		public JLabel addLabel(String label, double weight)
		{
			JLabel jl = new JLabel(label);
			addMiddle(jl, weight);

			return jl;
		}

		public void addSpace(Number space)
		{
			addLabel(StringUtils.repeat(' ', space.intValue()));
		}

		public void addLast(Component field)
		{
			addLast(field, DEFAULT_WEIGHT);
		}

		public void addLast(Component field, double weight)
		{
			cLast.weightx = weight;

			GridBagLayout gbl = (GridBagLayout) getLayout();
			gbl.setConstraints(field, cLast);

			add(field);
		}

		public void addMiddle(Component field)
		{
			addMiddle(field, DEFAULT_WEIGHT);
		}

		public void addMiddle(Component field, double weight)
		{
			cMiddle.weightx = weight;

			GridBagLayout gbl = (GridBagLayout) getLayout();
			gbl.setConstraints(field, cMiddle);

			add(field);
		}
	}

	protected static class EnableablePanel extends FormPanel {

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

	protected static class NumberInput extends TextInput {

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

	protected static class CheckBoxTitledBorder extends AbstractBorder {

		private static final long serialVersionUID = - 5180386633364648300L;

		private final TitledBorder _parent;
		private final JCheckBox _checkBox;

		public CheckBoxTitledBorder(String title, boolean selected) {

			_parent = BorderFactory.createTitledBorder(title);
			_checkBox = new JCheckBox(title, selected);
			_checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
		}

		public boolean isSelected()
		{
			return _checkBox.isSelected();
		}

		public void setSelected(boolean enabled)
		{
			_checkBox.setSelected(enabled);
		}

		public void addActionListener(ActionListener listener)
		{
			_checkBox.addActionListener(listener);
		}

		@Override
		public boolean isBorderOpaque()
		{
			return true;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			Insets borderInsets = _parent.getBorderInsets(c);
			Insets insets = getBorderInsets(c);
			int temp = (insets.top - borderInsets.top) / 2;
			_parent.paintBorder(c, g, x, y + temp, width, height - temp);
			Dimension size = _checkBox.getPreferredSize();
			final Rectangle rectangle = new Rectangle(5, 0, size.width, size.height);

			final Container container = (Container) c;
			container.addMouseListener(new MouseAdapter() {

				private void dispatchEvent(MouseEvent me)
				{
					if (rectangle.contains(me.getX(), me.getY())) {
						Point pt = me.getPoint();
						pt.translate(- 5, 0);
						_checkBox.setBounds(rectangle);
						_checkBox.dispatchEvent(new MouseEvent(_checkBox, me.getID(),
							me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
						if (! _checkBox.isValid()) {
							container.repaint();
						}
					}
				}

				public void mousePressed(MouseEvent me)
				{
					dispatchEvent(me);
				}

				public void mouseReleased(MouseEvent me)
				{
					dispatchEvent(me);
				}
			});

			SwingUtilities.paintComponent(g, _checkBox, container, rectangle);
		}

		@Override
		public Insets getBorderInsets(Component c)
		{
			Insets insets = _parent.getBorderInsets(c);
			insets.top = Math.max(insets.top, _checkBox.getPreferredSize().height);
			return insets;
		}
	}
}
