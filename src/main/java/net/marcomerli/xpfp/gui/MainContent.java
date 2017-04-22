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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MainContent extends JPanel {

	private static final long serialVersionUID = - 8732614615105930839L;

	private MainWindow win;

	public MainContent(MainWindow win) {

		super(new BorderLayout());
		this.win = win;

		FlightPlan flightPlan = Context.getFlightPlan();

		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(flightPlan.getName()),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(this);

		// TODO: build flight plan into table

		JButton export = new JButton("Export");
		export.addActionListener(new OnExport());
		layout.row().grid().add(export);
	}

	public class OnExport implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e)
		{
			try {
				FlightPlan flightPlan = Context.getFlightPlan();

				File fms = new File(Context.getSettings().getFMSDirectory(),
					flightPlan.getFilename());

				if (fms.exists()) {
					int select = GuiFn.selectPopup("FMS file already exists. Override it?", win);
					if (select == JOptionPane.NO_OPTION)
						return;
				}

				new FMSWriter(fms).write(flightPlan);
				GuiFn.infoPopup("Export completed", win);
			}
			catch (Exception ee) {
				GuiFn.errorPopup(ee, win);
			}
		}
	}
}
