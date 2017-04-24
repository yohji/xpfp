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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;
import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.FormatFn;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MainContent extends JPanel {

	private static final long serialVersionUID = - 8732614615105930839L;
	private static final Logger logger = Logger.getLogger(MainContent.class);

	private MainWindow win;

	public MainContent(MainWindow win) {

		super(new BorderLayout());
		this.win = win;

		FlightPlan flightPlan = Context.getFlightPlan();

		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(flightPlan.getName()),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid().add(new FlightPlaneTable());
		layout.row().bar()
			.add(new JLabel("Distance: " + FormatFn.distance(flightPlan.getDistance())), Tag.RIGHT)
			.gap()
			.add(new JLabel("ETE: " + FormatFn.time(flightPlan.getEte())), Tag.RIGHT);

		JButton export = new JButton("Export");
		export.addActionListener(new OnExport());
		layout.row().grid().add(export);
	}

	private class FlightPlaneTable extends JScrollPane {

		private static final long serialVersionUID = - 2408834160839600983L;

		private final String[] columnNames = new String[] {
			"-", "Bearing", "Identifier", "Type", "Country", "Latitude",
			"Longitude", "Elevation", "Distance", "ETE"
		};

		private final int[] columnWidths = new int[] {
			25, 50, 65, 40, 50, 110, 110, 80, 70, 70
		};

		public FlightPlaneTable() {

			FlightPlan flightPlan = Context.getFlightPlan();
			String[][] data = new String[flightPlan.size()][columnNames.length];

			int iRow = 0;
			for (Iterator<Waypoint> it = flightPlan.iterator(); it.hasNext(); iRow += 1) {
				Waypoint wp = it.next();
				int iCol = 0;

				data[iRow][iCol++] = String.valueOf(iRow + 1);
				data[iRow][iCol++] = FormatFn.degree(wp.getBearing());
				data[iRow][iCol++] = wp.getIdentifier();
				data[iRow][iCol++] = wp.getType().name();
				data[iRow][iCol++] = StringUtils.defaultString(wp.getCountry(), "-");

				Location loc = wp.getLocation();
				data[iRow][iCol++] = loc.getLatitude();
				data[iRow][iCol++] = loc.getLongitude();
				data[iRow][iCol++] = loc.getAltitude();

				data[iRow][iCol++] = FormatFn.distance(wp.getDistance());
				data[iRow][iCol++] = FormatFn.time(wp.getEte());
			}

			JTable table = new JTable(new DefaultTableModel(data, columnNames));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setFillsViewportHeight(true);
			table.setPreferredScrollableViewportSize(new Dimension(630, 100));

			TableColumnModel columnModel = table.getColumnModel();
			for (int iCol = 0; iCol < columnNames.length; iCol++) {
				TableColumn column = columnModel.getColumn(iCol);
				column.setPreferredWidth(columnWidths[iCol]);
			}

			setViewportView(table);
		}
	}

	private class OnExport implements ActionListener {

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
				logger.error("onError", ee);
				GuiFn.errorPopup(ee, win);
			}
		}
	}
}
