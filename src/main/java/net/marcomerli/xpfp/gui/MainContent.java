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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;
import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.FormatFn;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.fn.UnitFn;
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
	private FlightPlaneData data;

	public MainContent(MainWindow win) {

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.win = win;

		add(data = new FlightPlaneData());
		add(new FlightPlaneProcessor());
	}

	private class FlightPlaneData extends JPanel {

		private static final long serialVersionUID = - 2408834160839600983L;

		private final String[] columnNames = new String[] {
			"-", "Bearing", "Identifier", "Type", "Country", "Latitude",
			"Longitude", "Elevation", "Distance", "ETE"
		};

		private final int[] columnWidths = new int[] {
			25, 50, 65, 40, 50, 110, 110, 80, 70, 70
		};

		private JTable table;
		private ValueLabel distance;
		private ValueLabel ete;

		public FlightPlaneData() {

			super(new BorderLayout());

			FlightPlan flightPlan = Context.getFlightPlan();
			setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(flightPlan.getName()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));

			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			table.setFillsViewportHeight(true);
			table.setPreferredScrollableViewportSize(new Dimension(600, 100));

			JScrollPane pane = new JScrollPane();
			pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			pane.setViewportView(table);

			DesignGridLayout layout = new DesignGridLayout(this);
			layout.row().grid().add(pane);
			layout.row().bar()
				.add(distance = new ValueLabel("Distance", "-"), Tag.RIGHT).gap()
				.add(ete = new ValueLabel("ETE", "-"), Tag.RIGHT);

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
	}

	private class FlightPlaneProcessor extends JPanel {

		private static final long serialVersionUID = - 7914800898847981824L;
		private NumberInput fl;
		private NumberInput cs;
		private NumberInput vs;
		private JButton export;

		public FlightPlaneProcessor() {

			super(new BorderLayout());

			setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Flight Data"),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));

			Preferences prefs = Context.getPreferences();
			DesignGridLayout layout = new DesignGridLayout(this);

			fl = new NumberInput(3);
			fl.setText(prefs.getProperty(Preferences.FP_FLIGHT_LEVEL));
			cs = new NumberInput(3);
			cs.setText(prefs.getProperty(Preferences.FP_CRUISING_SPEED));
			vs = new NumberInput(4);
			vs.setText(prefs.getProperty(Preferences.FP_VERTICAL_SPEED));

			JButton calc = new JButton("Calculate");
			calc.addActionListener(new OnCalculate());

			export = new JButton("Export");
			export.setEnabled(false);
			export.addActionListener(new OnExport());

			layout.row().grid().add(new JLabel("Flight level (FL)")).add(fl)
				.add(new JLabel("Cruising Speed (kn)")).add(cs)
				.add(new JLabel("Vertical Speed (ft/s)")).add(vs)
				.add(calc).add(export);
		}

		private class OnCalculate implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					FlightPlan flightPlan = Context.getFlightPlan();
					flightPlan.calculate(
						UnitFn.ftToM(Integer.valueOf(fl.getText()) * 100),
						UnitFn.knToMs(Integer.valueOf(cs.getText())),
						UnitFn.ftToM(Integer.valueOf(vs.getText())));

					Preferences prefs = Context.getPreferences();
					prefs.setProperty(Preferences.FP_FLIGHT_LEVEL, fl.getText());
					prefs.setProperty(Preferences.FP_CRUISING_SPEED, cs.getText());
					prefs.setProperty(Preferences.FP_VERTICAL_SPEED, vs.getText());
					prefs.save();

					data.refresh();
					export.setEnabled(true);
				}
				catch (Exception ee) {
					logger.error("OnCalculate", ee);
					GuiFn.errorPopup(ee, win);
				}
			}
		}

		private class OnExport implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					FlightPlan flightPlan = Context.getFlightPlan();
					File fms = new File(Context.getSettings().getProperty(Settings.EXPORT_DIRECTORY, File.class),
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

	private class NumberInput extends JTextField {

		private static final long serialVersionUID = - 3400518930083189803L;

		public NumberInput(int maxSize) {

			super(maxSize);

			addKeyListener(new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e)
				{
					String text = NumberInput.this.getText();
					char key = e.getKeyChar();

					if (text.isEmpty() || (key == 127 || key == 8))
						return;

					if ((key < 48 || key > 57) || text.length() > maxSize)
						setText(text.substring(0, text.length() - 1));
				}
			});
		}
	}

	private class ValueLabel extends JLabel {

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
