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

package net.marcomerli.xpfms.fn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import com.google.maps.ElevationApi;
import com.google.maps.GeoApiContext;

import net.marcomerli.xpfms.model.Location;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class GeoApiFn {

	private static final GeoApiContext context;
	static {
		// TODO: move the key and proxy into configuration
		context = new GeoApiContext().setApiKey("AIzaSyA2dgxtI3wXcuxk8f_T3a_iKPlcpoeev2s")
			.setProxy(new Proxy(Type.HTTP, new InetSocketAddress("localhost", 3128)));
	}

	public static double elevationOf(Location location) throws Exception
	{
		return ElevationApi.getByPoint(context, location)
			.await().elevation;
	}

	public static double distanceOf(Location a, Location b) throws Exception
	{
		return - 1.0; // TODO: continue...
	}

	private GeoApiFn() {}
}
