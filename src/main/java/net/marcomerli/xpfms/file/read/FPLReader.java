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

package net.marcomerli.xpfms.file.read;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.marcomerli.xpfms.model.FlightPlan;
import net.marcomerli.xpfms.model.Location;
import net.marcomerli.xpfms.model.Waypoint;
import net.marcomerli.xpfms.model.WaypointType;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FPLReader extends Reader {

	public FPLReader(File file) {

		super(file);
	}

	@Override
	public FlightPlan read() throws Exception
	{
		Validate.isTrue(file.getName().endsWith(".fpl") || file.getName().endsWith(".FPL"),
			"Invalid FPL file format.");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName("flight-plan");
		Validate.isTrue(nodes.getLength() == 1, "Invalid FPL file format.");

		nodes = doc.getElementsByTagName("route-name");
		Validate.isTrue(nodes.getLength() == 1, "FPL file has not a name of the route.");

		FlightPlan flightPlan = new FlightPlan(nodes.item(0).getFirstChild().getNodeValue());

		NodeList wps = doc.getElementsByTagName("waypoint");
		Validate.isTrue(wps.getLength() > 0, "FPL file has not waypoints.");

		for (int iWp = 0; iWp < wps.getLength(); iWp++) {
			NodeList wpData = wps.item(iWp).getChildNodes();

			Waypoint waypoint = new Waypoint();
			Double lat = null;
			Double lng = null;

			for (int iData = 0; iData < wpData.getLength(); iData++) {
				Node data = wpData.item(iData).getNextSibling();
				if (data != null) {

					String value = data.getTextContent();

					switch (data.getNodeName()) {
					case "identifier":
						waypoint.setIdentifier(value);
					break;
					case "type":
						waypoint.setType(WaypointType.get(value));
					break;
					case "country-code":
						waypoint.setCountry(value);
					break;
					case "lat":
						lat = new Double(value);
					break;
					case "lon":
						lng = new Double(value);
					}
				}
			}

			waypoint.setLocation(new Location(lat, lng));
			flightPlan.add(waypoint);
		}

		return flightPlan;
	}
}
