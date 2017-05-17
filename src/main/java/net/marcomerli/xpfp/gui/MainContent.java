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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.error.DataException;
import net.marcomerli.xpfp.error.FlightPlanException;
import net.marcomerli.xpfp.error.GeoException;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.FormatFn;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.fn.UnitFn;
import net.marcomerli.xpfp.gui.Components.EnableablePanel;
import net.marcomerli.xpfp.gui.Components.FormPanel;
import net.marcomerli.xpfp.gui.Components.NumberInput;
import net.marcomerli.xpfp.gui.Components.ResetFormAction;
import net.marcomerli.xpfp.gui.Components.TextInput;
import net.marcomerli.xpfp.gui.Components.ValidateFormAction;
import net.marcomerli.xpfp.gui.Components.ValueLabel;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MainContent extends JPanel {

	private static final long serialVersionUID = - 8732614615105930839L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SKYVECTOR_AIRPORT_URL = "https://skyvector.com/airport/";

	private MainWindow win;
	private FlightPlaneData data;
	private FlightPlaneProcessor processor;

	public MainContent(MainWindow win) {

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.win = win;

		add(data = new FlightPlaneData());
		add(processor = new FlightPlaneProcessor());
	}

	private class FlightPlaneData extends JPanel {

		private static final long serialVersionUID = - 2408834160839600983L;
		private static final int TABLE_HEIGHT = 223;

		private final String[] columnNames = new String[] {
			"#", "Identifier", "Type", "Country", "Latitude",
			"Longitude", "Elevation", "Bearing", "Heading",
			"Distance", "ETE"
		};

		private final int[] columnWidths = new int[] {
			25, 65, 50, 50, 115, 115, 60, 55, 55, 90, 65
		};

		private JTable table;
		private ValueLabel distance;
		private ValueLabel ete;

		public FlightPlaneData() {

			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

			FlightPlan flightPlan = Context.getFlightPlan();
			setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(flightPlan.getName()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));

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

						int select = GuiFn.selectDialog(String.format("Do you want to remove the #%d waypoint?", (row + 1)), win);
						if (select == JOptionPane.NO_OPTION || select == JOptionPane.CLOSED_OPTION)
							return;

						Context.getFlightPlan().remove(row);
						processor.getCalculate().doClick();
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
							GuiFn.fatalDialog(e, win);
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

	private class FlightPlaneProcessor extends JPanel {

		private static final long serialVersionUID = - 7914800898847981824L;

		private NumberInput crzLevel;
		private NumberInput crzSpeed;
		private NumberInput clbRate;
		private NumberInput clbSpeed;
		private NumberInput desRate;
		private NumberInput desSpeed;
		private TextInput filename;
		private EnableablePanel vnavPanel;
		private JButton export;
		private JButton calculate;

		public FlightPlaneProcessor() {

			super(new FlowLayout(FlowLayout.LEFT));
			Preferences prefs = Context.getPreferences();

			crzLevel = new NumberInput(3);
			crzLevel.setText(prefs.getProperty(Preferences.FP_CRZ_LEVEL));
			crzSpeed = new NumberInput(3);
			crzSpeed.setText(prefs.getProperty(Preferences.FP_CRZ_SPEED));
			clbRate = new NumberInput(5);
			clbRate.setText(prefs.getProperty(Preferences.FP_CLB_RATE));
			clbSpeed = new NumberInput(3);
			clbSpeed.setText(prefs.getProperty(Preferences.FP_CLB_SPEED));
			desRate = new NumberInput(5);
			desRate.setText(prefs.getProperty(Preferences.FP_DES_RATE));
			desSpeed = new NumberInput(3);
			desSpeed.setText(prefs.getProperty(Preferences.FP_DES_SPEED));

			calculate = new JButton("Calculate");
			calculate.addActionListener(new OnCalculate(crzLevel, crzSpeed,
				clbRate, clbSpeed, desRate, desSpeed));

			filename = new TextInput();
			filename.setColumns(15);
			filename.setText(Context.getFlightPlan().getFilename());

			JButton reset = new JButton("Reset");
			reset.addActionListener(new OnReset(crzLevel, crzSpeed,
				clbRate, clbSpeed, desRate, desSpeed));

			export = new JButton("Export");
			export.setEnabled(false);
			export.addActionListener(new OnExport(filename));

			FormPanel cruisePanel = new FormPanel();
			cruisePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Cruise Navigation"),
				BorderFactory.createEmptyBorder()));

			cruisePanel.addLabel("Flight level (FL)").setLabelFor(crzLevel);
			cruisePanel.addLast(crzLevel);
			cruisePanel.addLabel("Cruising speed (GS kn)").setLabelFor(crzSpeed);
			cruisePanel.addLast(crzSpeed);

			vnavPanel = new EnableablePanel("Vertical Navigation");

			vnavPanel.addLabel("Rate of climb (ft/min)").setLabelFor(clbRate);
			vnavPanel.addLast(clbRate);
			vnavPanel.addLabel("Climbing speed (GS kn)").setLabelFor(clbSpeed);
			vnavPanel.addLast(clbSpeed);
			vnavPanel.addLabel("Rate of descent (ft/min)").setLabelFor(desRate);
			vnavPanel.addLast(desRate);
			vnavPanel.addLabel("Descenting speed (GS kn)").setLabelFor(desSpeed);
			vnavPanel.addLast(desSpeed);
			vnavPanel.setEnabled(prefs.getProperty(Preferences.FP_VNAV, Boolean.class));

			JPanel divPanel = new JPanel();
			divPanel.setLayout(new BoxLayout(divPanel, BoxLayout.PAGE_AXIS));
			divPanel.add(Box.createRigidArea(new Dimension(50, 0)));
			divPanel.add(new JSeparator(JSeparator.VERTICAL));
			divPanel.add(Box.createRigidArea(new Dimension(50, 0)));
			divPanel.add(Box.createGlue());

			FormPanel actionPanel = new FormPanel();
			actionPanel.addMiddle(reset);
			actionPanel.addLast(calculate);
			actionPanel.addLast(new JSeparator(JSeparator.HORIZONTAL));
			actionPanel.addLabel("Filename").setLabelFor(filename);
			actionPanel.addLast(filename);
			actionPanel.addSpace(1);
			actionPanel.addLast(export);

			add(cruisePanel);
			add(vnavPanel);
			add(divPanel);
			add(actionPanel);
		}

		public JButton getCalculate()
		{
			return calculate;
		}

		private class OnCalculate extends ValidateFormAction {

			public OnCalculate(JComponent... fields) {

				super(fields);
			}

			@Override
			public void perform(ActionEvent e)
			{
				try {
					FlightPlan flightPlan = Context.getFlightPlan();

					if (vnavPanel.isEnabled()) {
						flightPlan.calculate(
							UnitFn.ftToM(Integer.valueOf(crzLevel.getText()) * 100),
							UnitFn.knToMs(Integer.valueOf(crzSpeed.getText())),
							UnitFn.ftMinToMs(Integer.valueOf(clbRate.getText())),
							UnitFn.knToMs(Integer.valueOf(clbSpeed.getText())),
							UnitFn.ftMinToMs(Integer.valueOf(desRate.getText())),
							UnitFn.knToMs(Integer.valueOf(desSpeed.getText())));
					}
					else {
						flightPlan.calculate(
							UnitFn.ftToM(Integer.valueOf(crzLevel.getText()) * 100),
							UnitFn.knToMs(Integer.valueOf(crzSpeed.getText())));
					}

					Preferences prefs = Context.getPreferences();
					prefs.setProperty(Preferences.FP_VNAV, Boolean.toString(vnavPanel.isEnabled()));
					prefs.setProperty(Preferences.FP_CRZ_LEVEL, crzLevel.getText());
					prefs.setProperty(Preferences.FP_CRZ_SPEED, crzSpeed.getText());
					prefs.setProperty(Preferences.FP_CLB_RATE, clbRate.getText());
					prefs.setProperty(Preferences.FP_CLB_SPEED, clbSpeed.getText());
					prefs.setProperty(Preferences.FP_DES_RATE, desRate.getText());
					prefs.setProperty(Preferences.FP_DES_SPEED, desSpeed.getText());
					prefs.save();

					data.refresh();
					export.setEnabled(true);
				}
				catch (FlightPlanException | GeoException ee) {
					GuiFn.errorDialog(ee, win);
				}
				catch (Exception ee) {
					logger.error("onCalculate", ee);
					GuiFn.fatalDialog(ee, win);
				}
			}
		}

		private class OnReset extends ResetFormAction {

			public OnReset(JComponent... fields) {

				super(fields);
			}

			@Override
			public void perform(ActionEvent e)
			{
				try {
					Preferences prefs = Context.getPreferences();
					prefs.clearProperty(Preferences.FP_CRZ_LEVEL);
					prefs.clearProperty(Preferences.FP_CRZ_SPEED);
					prefs.clearProperty(Preferences.FP_CLB_RATE);
					prefs.clearProperty(Preferences.FP_CLB_SPEED);
					prefs.clearProperty(Preferences.FP_DES_RATE);
					prefs.clearProperty(Preferences.FP_DES_SPEED);
					prefs.save();
				}
				catch (DataException ee) {
					GuiFn.errorDialog(ee, win);
				}
			}
		}

		private class OnExport extends ValidateFormAction {

			public OnExport(JComponent... fields) {

				super(fields);
			}

			@Override
			public void perform(ActionEvent e)
			{
				try {
					FlightPlan flightPlan = Context.getFlightPlan();
					flightPlan.setFilename(filename.getText());

					FMSWriter writer = new FMSWriter(Context.getSettings()
						.getProperty(Settings.DIR_EXPORT, File.class), flightPlan.getFilename());

					if (writer.exists()) {
						int select = GuiFn.selectDialog("FMS file already exists. Override it?", win);
						if (select == JOptionPane.NO_OPTION || select == JOptionPane.CLOSED_OPTION)
							return;
					}

					writer.write(flightPlan);
					GuiFn.infoDialog("Export completed", win);
				}
				catch (Exception ee) {
					logger.error("onError", ee);
					GuiFn.errorDialog(ee, win);
				}
			}
		}
	}
}
