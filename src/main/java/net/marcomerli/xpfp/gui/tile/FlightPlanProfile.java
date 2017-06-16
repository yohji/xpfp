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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.fn.FormatFn;
import net.marcomerli.xpfp.fn.GeoFn;
import net.marcomerli.xpfp.fn.GuiFn;
import net.marcomerli.xpfp.fn.UnitFn;
import net.marcomerli.xpfp.gui.MainContent;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FlightPlanProfile extends JPanel {

	private static final long serialVersionUID = - 2346452225583534510L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private MainContent parent;

	public FlightPlanProfile(MainContent parent) {

		this.parent = parent;

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Elevation profile"),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		add(new Graph());
	}

	public void refresh()
	{
		revalidate();
		repaint();
	}

	private class Graph extends JPanel {

		private static final long serialVersionUID = 3533883565773413594L;

		private static final int GRAPH_HEIGHT = 145;
		private static final int SEA_LEVEL = 10;

		private FlightPlan plan = Context.getFlightPlan();
		private Ellipse2D.Double[] waypoints;
		private Dimension size;

		public Graph() {

			ToolTipManager toolTip = ToolTipManager.sharedInstance();
			toolTip.registerComponent(this);
			toolTip.setInitialDelay(100);
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			try {
				setup(g);

				Location[] path = new Location[Context.getFlightPlan().size()];
				int iLoc = 0;
				for (Waypoint wp : plan)
					path[iLoc++] = wp.getLocation();

				double[] elevs = GeoFn.elevations(path);
				for (int iElev = 0; iElev < elevs.length; iElev++)
					if (elevs[iElev] < 0)
						elevs[iElev] = 0;

				double fl = 0;
				double yMax = NumberUtils.max(elevs);
				if (plan.isValid()) {
					fl = new Integer(parent.getCrzLevel()) * 100.0;
					fl = UnitFn.ftToM(fl);

					if (fl > yMax)
						yMax = fl;
				}

				double xFactor = (double) size.width / elevs.length;
				double yFactor = size.height / (yMax + (yMax * 0.15));

				elevationMarker(g, yFactor);
				elevationProfile(g, elevs, xFactor, yFactor);
				seaLevel(g);

				if (plan.isValid())
					flightPlan(g, fl, yFactor);
			}
			catch (Exception ee) {
				logger.error("onGraph", ee);
				GuiFn.fatalDialog(ee, parent.getWin());
			}
		}

		private void flightPlan(Graphics g, double fl, double yFactor)
		{
			double xFactor;
			g.setColor(Color.RED);

			if (parent.isVNavEnabled()) {

				double dist = 0;
				int[] dists = new int[plan.size()];

				for (int iWp = 0; iWp < plan.size(); iWp++) {
					Waypoint wp = plan.get(iWp);

					dist += wp.getDistance();
					dists[iWp] = (int) dist;
				}

				xFactor = (double) size.width / dist;

				for (int iWp = 0; iWp < (plan.size() - 1); iWp++) {

					g.drawLine(
						x(dists[iWp], xFactor),
						y(plan.get(iWp).getLocation().alt, yFactor),
						x(dists[iWp + 1], xFactor),
						y(plan.get(iWp + 1).getLocation().alt, yFactor));
				}

				Graphics2D g2 = (Graphics2D) g;
				waypoints = new Ellipse2D.Double[plan.size()];

				for (int iWp = 0; iWp < plan.size(); iWp++) {
					int px = x(dists[iWp], xFactor);
					int py = y(plan.get(iWp).getLocation().alt, yFactor);

					g2.fill(new Ellipse2D.Double(px, py - 2, 5, 5));
					waypoints[iWp] = new Ellipse2D.Double(px, py - 6, 12, 12);
				}
			}
			else {
				g.drawLine(
					0, y(fl, yFactor),
					size.width, y(fl, yFactor));
			}
		}

		@Override
		public String getToolTipText(MouseEvent event)
		{
			if (waypoints != null) {
				Point p = new Point(event.getX(), event.getY());

				for (int iWp = 0; iWp < waypoints.length; iWp++) {
					Double mark = waypoints[iWp];
					if (mark.contains(p))
						return plan.get(iWp).getIdentifier();
				}
			}

			return super.getToolTipText(event);
		}

		private void seaLevel(Graphics g)
		{
			g.setColor(new Color(0, 0, 255, 170));
			g.drawLine(
				0, size.height,
				size.width, size.height);
		}

		private void elevationProfile(Graphics g, double[] elevs,
			double xFactor, double yFactor)
		{
			g.setColor(Color.BLACK);
			int lx = 0;
			double ly = 0;
			for (int x = 0; x < elevs.length; x++) {
				double y = elevs[x];

				g.drawLine(
					x(lx, xFactor), y(ly, yFactor),
					x(x, xFactor), y(y, yFactor));

				lx = x;
				ly = y;
			}
		}

		private void elevationMarker(Graphics g, double yFactor)
		{
			g.setColor(new Color(128, 128, 128, 128));
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
			for (int y = 0; y < size.height; y += 20) {
				int dy = size.height - y;

				g.drawLine(0, dy, size.width, dy);
				g.drawString(FormatFn.altitude(
					UnitFn.mToFt(y / yFactor)), 5, dy);
			}
		}

		private void setup(Graphics g)
		{
			size = getPreferredSize();
			size.setSize(size.width - 15, size.height - 5);

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, size.width, size.height);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, size.width, size.height);

			size.setSize(size.width, size.height - SEA_LEVEL);
		}

		private int x(int x, double factor)
		{
			return (int) (x * factor);
		}

		private int y(double y, double factor)
		{
			return size.height - (int) (y * factor);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(parent.getTableWidth() - 10, GRAPH_HEIGHT);
		}
	}
}
