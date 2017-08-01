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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.data.Preferences;
import net.marcomerli.xpfp.core.data.Settings;
import net.marcomerli.xpfp.error.DataException;
import net.marcomerli.xpfp.error.FlightPlanException;
import net.marcomerli.xpfp.error.GeoException;
import net.marcomerli.xpfp.error.NetworkException;
import net.marcomerli.xpfp.file.write.FMSWriter;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.fn.UnitFn;
import net.marcomerli.xpfp.gui.MainContent;
import net.marcomerli.xpfp.gui.comp.EnableablePanel;
import net.marcomerli.xpfp.gui.comp.FormPanel;
import net.marcomerli.xpfp.gui.comp.NumberInput;
import net.marcomerli.xpfp.gui.comp.ResetFormAction;
import net.marcomerli.xpfp.gui.comp.TextInput;
import net.marcomerli.xpfp.gui.comp.ValidateFormAction;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FlightPlanData extends JPanel {

	private static final long serialVersionUID = - 7914800898847981824L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private MainContent parent;

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

	public FlightPlanData(MainContent parent) {

		this.parent = parent;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
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
		filename.setColumns(25);
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
		vnavPanel.addMiddle(clbRate);
		vnavPanel.addLabel("Climbing speed (GS kn)").setLabelFor(clbSpeed);
		vnavPanel.addLast(clbSpeed);

		vnavPanel.addLabel("Rate of descent (ft/min)").setLabelFor(desRate);
		vnavPanel.addMiddle(desRate);
		vnavPanel.addLabel("Descenting speed (GS kn)").setLabelFor(desSpeed);
		vnavPanel.addLast(desSpeed);
		vnavPanel.setEnabled(prefs.getProperty(Preferences.FP_VNAV, Boolean.class));

		FormPanel actionPanel = new FormPanel();
		actionPanel.addLast(calculate);
		actionPanel.addLast(reset);

		FormPanel exportPanel = new FormPanel();
		exportPanel.addSpace(95);
		exportPanel.addLabel("Filename").setLabelFor(filename);
		exportPanel.addMiddle(filename);
		exportPanel.addLast(export);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(cruisePanel);
		top.add(vnavPanel);
		top.add(actionPanel);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottom.add(exportPanel);

		add(top);
		add(new JSeparator(JSeparator.HORIZONTAL));
		add(bottom);
	}

	public void calculate()
	{
		calculate.doClick();
	}

	public String getCrzLevel()
	{
		return crzLevel.getText();
	}

	public boolean isVNavEnabled()
	{
		return vnavPanel.isEnabled();
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

				export.setEnabled(true);
			}
			catch (FlightPlanException | GeoException | NetworkException ee) {
				export.setEnabled(false);
				GuiFn.errorDialog(ee, parent.getWin());
			}
			catch (Exception ee) {
				logger.error("onCalculate", ee);
				GuiFn.fatalDialog(ee, parent.getWin());
			}
			finally {
				parent.refresh();
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
				GuiFn.errorDialog(ee, parent.getWin());
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
					int select = GuiFn.selectDialog("FMS file already exists. Override it?", parent.getWin());
					if (select == JOptionPane.NO_OPTION || select == JOptionPane.CLOSED_OPTION)
						return;
				}

				writer.write(flightPlan);
				GuiFn.infoDialog("Export completed", parent.getWin());
			}
			catch (Exception ee) {
				logger.error("onError", ee);
				GuiFn.errorDialog(ee, parent.getWin());
			}
		}
	}
}
