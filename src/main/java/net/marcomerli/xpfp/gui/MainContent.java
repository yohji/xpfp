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

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.marcomerli.xpfp.gui.comp.tile.FlightPlanData;
import net.marcomerli.xpfp.gui.comp.tile.FlightPlanProfile;
import net.marcomerli.xpfp.gui.comp.tile.FlightPlanTable;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class MainContent extends JPanel {

	private static final long serialVersionUID = - 8732614615105930839L;

	private MainWindow win;
	private FlightPlanTable table;
	private FlightPlanProfile profile;
	private FlightPlanData data;

	public MainContent(MainWindow win) {

		this.win = win;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(Window.PADDING_BORDER);

		add(table = new FlightPlanTable(this));
		add(profile = new FlightPlanProfile(this));
		add(data = new FlightPlanData(this));
	}

	public MainWindow getWin()
	{
		return win;
	}

	public void refresh()
	{
		table.refresh();
		profile.refresh();
	}

	public void calculate()
	{
		data.getCalculate().doClick();
	}

	public String getCrzLevel()
	{
		return data.getCrzLevel();
	}

	public boolean isVNavEnabled()
	{
		return data.isVNavEnabled();
	}

	public int getTableWidth()
	{
		return table.getWidth();
	}
}
