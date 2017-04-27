/**
 *  Copyright (c) 2017 Marco Merli <yohji@marcomerli.net>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package net.marcomerli.xpfp.gui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Window extends JFrame {

	private static final long serialVersionUID = 7604558040904896370L;
	protected final Logger logger = Logger.getLogger(getClass());

	public static final String TITLE_FULL = "X-Plane Flight Planner";
	public static final String TITLE_COMPACT = "XPFP";

	public Window(String title) throws HeadlessException {

		super(title);

		URL res = getClass().getClassLoader().getResource("xpfp.jpg");
		setIconImage(new ImageIcon(res).getImage());
	}

	/**
	 * @author Bozhidar Batsov
	 */
	protected static class EditMenuMouseListener extends MouseAdapter {

		private JTextComponent textComponent;
		private JPopupMenu popup = new JPopupMenu();

		private Action cutAction;
		private Action copyAction;
		private Action pasteAction;
		private Action selectAllAction;

		@SuppressWarnings("serial")
		public EditMenuMouseListener(JTextComponent textComponent) {

			this.textComponent = textComponent;

			cutAction = new AbstractAction("Cut") {

				@Override
				public void actionPerformed(ActionEvent ae)
				{
					textComponent.cut();
				}
			};

			popup.add(cutAction);

			copyAction = new AbstractAction("Copy") {

				@Override
				public void actionPerformed(ActionEvent ae)
				{
					textComponent.copy();
				}
			};

			popup.add(copyAction);

			pasteAction = new AbstractAction("Paste") {

				@Override
				public void actionPerformed(ActionEvent ae)
				{
					textComponent.paste();
				}
			};

			popup.add(pasteAction);
			popup.addSeparator();

			selectAllAction = new AbstractAction("Select All") {

				@Override
				public void actionPerformed(ActionEvent ae)
				{
					textComponent.selectAll();
				}
			};

			popup.add(selectAllAction);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				if (! (e.getSource() instanceof JTextComponent)) {
					return;
				}

				textComponent = (JTextComponent) e.getSource();
				textComponent.requestFocus();

				boolean enabled = textComponent.isEnabled();
				boolean editable = textComponent.isEditable();
				boolean nonempty = ! (textComponent.getText() == null || textComponent.getText().equals(""));
				boolean marked = textComponent.getSelectedText() != null;

				boolean pasteAvailable = Toolkit.getDefaultToolkit().getSystemClipboard()
					.getContents(null).isDataFlavorSupported(DataFlavor.stringFlavor);

				cutAction.setEnabled(enabled && editable && marked);
				copyAction.setEnabled(enabled && marked);
				pasteAction.setEnabled(enabled && editable && pasteAvailable);
				selectAllAction.setEnabled(enabled && nonempty);

				int nx = e.getX();

				if (nx > 500) {
					nx = nx - popup.getSize().width;
				}

				popup.show(e.getComponent(), nx, e.getY() - popup.getSize().height);
			}
		}
	}
}
