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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class CheckBoxTitledBorder extends AbstractBorder {

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
