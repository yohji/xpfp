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

package net.marcomerli.xpfp.gui.tile;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.fn.FormatFn;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.gui.MainContent;
import net.marcomerli.xpfp.gui.comp.ValueLabel;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FlightPlanTable extends JPanel {

	private static final long serialVersionUID = - 2408834160839600983L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SKYVECTOR_AIRPORT_URL = "https://skyvector.com/airport/";
	private static final int TABLE_HEIGHT = 160;

	private final String[] columnNames = new String[] {
		"#", "Identifier", "Type", "Country", "Latitude",
		"Longitude", "Elevation", "Bearing", "Heading",
		"Distance", "ETE"
	};

	private final int[] columnWidths = new int[] {
		25, 65, 50, 50, 115, 115, 60, 55, 55, 90, 65
	};

	private MainContent parent;

	private JTable table;
	private ValueLabel distance;
	private ValueLabel ete;

	public FlightPlanTable(MainContent parent) {

		this.parent = parent;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		FlightPlan flightPlan = Context.getFlightPlan();
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(flightPlan.getName()),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		int width = 0;
		for (int i = 0; i < columnWidths.length; i++)
			width += columnWidths[i];

		table = new JTable();
		table.setPreferredScrollableViewportSize(new Dimension(width, TABLE_HEIGHT));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new TablePopup());

		JScrollPane pane = new JScrollPane();
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pane.setViewportView(table);

		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.LINE_AXIS));
		info.add(distance = new ValueLabel("Distance", "-"));
		info.add(new JLabel(" | "));
		info.add(ete = new ValueLabel("ETE", "-"));

		add(pane);
		add(info);
		render();
	}

	private void render()
	{
		FlightPlan flightPlan = Context.getFlightPlan();
		String[][] data = new String[flightPlan.size()][columnNames.length];

		int iRow = 0;
		for (Iterator<Waypoint> it = flightPlan.iterator(); it.hasNext(); iRow += 1) {
			Waypoint wp = it.next();
			int iCol = 0;

			data[iRow][iCol++] = String.valueOf(iRow + 1);
			data[iRow][iCol++] = wp.getIdentifier();
			data[iRow][iCol++] = wp.getType().name();
			data[iRow][iCol++] = StringUtils.defaultString(wp.getCountry(), "-");

			Location loc = wp.getLocation();
			data[iRow][iCol++] = loc.getLatitude();
			data[iRow][iCol++] = loc.getLongitude();
			data[iRow][iCol++] = loc.getAltitude();

			data[iRow][iCol++] = FormatFn.degree(wp.getBearing());
			data[iRow][iCol++] = FormatFn.degree(wp.getHeading());
			data[iRow][iCol++] = FormatFn.distance(wp.getDistance());
			data[iRow][iCol++] = FormatFn.time(wp.getEte());
		}

		table.setModel(new TableModel(data, columnNames));
		distance.setValue(FormatFn.distance(flightPlan.getDistance()));
		ete.setValue(FormatFn.time(flightPlan.getEte()));

		TableColumnModel columnModel = table.getColumnModel();
		for (int iCol = 0; iCol < columnNames.length; iCol++) {
			TableColumn column = columnModel.getColumn(iCol);
			column.setPreferredWidth(columnWidths[iCol]);
		}
	}

	public void refresh()
	{
		render();
		revalidate();
		repaint();
	}

	private class TablePopup extends MouseAdapter {

		private JPopupMenu popup = new JPopupMenu();
		private Action delete;
		private Action info;

		public TablePopup() {

			delete = new AbstractAction("Delete") {

				private static final long serialVersionUID = 8978260573053814935L;

				@Override
				public void actionPerformed(ActionEvent ee)
				{
					int row = table.getSelectedRow();

					int select = GuiFn.selectDialog(String.format("Do you want to remove the #%d waypoint?", (row + 1)), parent.getWin());
					if (select == JOptionPane.NO_OPTION || select == JOptionPane.CLOSED_OPTION)
						return;

					Context.getFlightPlan().remove(row);
					parent.calculate();
					refresh();
				}
			};

			popup.add(delete);
			popup.addSeparator();

			info = new AbstractAction("Info") {

				private static final long serialVersionUID = - 4915421040608026019L;

				@Override
				public void actionPerformed(ActionEvent ee)
				{
					try {
						Object icao = table.getModel().getValueAt(table.getSelectedRow(), 1);
						GuiFn.openBrowser(new URL(SKYVECTOR_AIRPORT_URL + icao));
					}
					catch (Exception e) {
						logger.error("info", e);
						GuiFn.fatalDialog(e, parent.getWin());
					}
				}
			};
			info.setEnabled(false);
			popup.add(info);
		}

		@Override
		public void mouseClicked(final MouseEvent e)
		{
			final int row = table.getSelectedRow();
			if (row == - 1)
				return;

			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {

				info.setEnabled(WaypointType.ICAO.name().equals(
					table.getModel().getValueAt(row, 2)));

				int nx = e.getX();
				if (nx > (table.getSize().width - popup.getSize().width))
					nx = nx - popup.getSize().width;

				popup.show(e.getComponent(), nx, e.getY());
			}
		}
	}

	private class TableModel extends DefaultTableModel {

		private static final long serialVersionUID = - 7234273051637878221L;

		public TableModel(String[][] data, String[] columnNames) {

			super(data, columnNames);
		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}
}
