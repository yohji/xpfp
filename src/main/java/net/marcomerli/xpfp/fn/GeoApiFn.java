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

package net.marcomerli.xpfp.fn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.sis.distance.DistanceUtils;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.core.Settings;
import net.marcomerli.xpfp.model.Location;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class GeoApiFn {

	private static final GeoApiContext context;
	static {
		// TODO: move the key into settings
		context = new GeoApiContext().setApiKey("AIzaSyA2dgxtI3wXcuxk8f_T3a_iKPlcpoeev2s");
	}

	public static void init()
	{
		Settings settings = Context.getSettings();
		if (settings.isProxyActive())
			context.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(
				settings.getProxyHostname(), settings.getProxyPort())));
	}

	public static double elevationOf(Location location) throws Exception
	{
		double elev = ElevationApi.getByPoint(context, location)
			.await().elevation;

		location.alt = elev;
		return elev;
	}

	public static double distanceOf(Location a, Location b)
	{
		double dist = DistanceUtils.getHaversineDistance(
			a.lat, a.lng,
			b.lat, b.lng);

		return dist * 1000;
	}

	private GeoApiFn() {}
}
