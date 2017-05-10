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

package net.marcomerli.xpfp.file.read;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FPLReader extends Reader {

	public FPLReader(File file) {

		super(file, FileType.FPL);
	}

	@Override
	public FlightPlan read() throws Exception
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nodes = doc.getElementsByTagName("flight-plan");
		validate(nodes.getLength() == 1, "Invalid FPL file format.");

		nodes = doc.getElementsByTagName("route-name");
		validate(nodes.getLength() == 1, "FPL file has not a name of the route.");

		FlightPlan flightPlan = new FlightPlan(nodes.item(0).getFirstChild().getNodeValue());

		NodeList wps = doc.getElementsByTagName("waypoint");
		validate(wps.getLength() > 0, "FPL file has not waypoints.");

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
						waypoint.setCountry(StringUtils.stripToNull(value));
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
