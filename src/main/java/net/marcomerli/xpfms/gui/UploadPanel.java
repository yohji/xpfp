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

package net.marcomerli.xpfms.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class UploadPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3073676923086516448L;

	private JFileChooser fc;
	private JButton upload;

	public UploadPanel() {

		super(new BorderLayout());

		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		upload = new JButton("Upload FPL file");
		upload.addActionListener(this);

		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(upload);

		add(buttonPanel, BorderLayout.PAGE_START);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == upload) {
			int returnVal = fc.showOpenDialog(upload);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File file = fc.getSelectedFile();
				JOptionPane.showMessageDialog(this, file.getAbsolutePath(),
					"X-Plane FMS", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
