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

package net.marcomerli.xpfp.fn;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.error.NetworkException;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class NetworkFn {

	private static final String INTERNET_ADDRESS_TEST = "www.google.com";
	private static final int DEFAULT_ACCESS_TIME = 1000;
	private static long LAST_INTERNET_ACCESS = 0;

	public static void requireInternet() throws NetworkException
	{
		long now = System.currentTimeMillis();
		if ((now - LAST_INTERNET_ACCESS) < DEFAULT_ACCESS_TIME) {
			LAST_INTERNET_ACCESS = now;
			return;
		}

		if (! hasInternet())
			throw new NetworkException("No internet connection available.");
	}

	public static boolean hasInternet()
	{
		try {
			InetAddress[] addresses = InetAddress.getAllByName(INTERNET_ADDRESS_TEST);
			if (addresses[0].isReachable(DEFAULT_ACCESS_TIME))
				return true;

			URLConnection conn = new URL("https://" + INTERNET_ADDRESS_TEST)
				.openConnection(Context.getProxy());
			conn.setConnectTimeout(DEFAULT_ACCESS_TIME);
			conn.connect();

			LAST_INTERNET_ACCESS = System.currentTimeMillis();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	private NetworkFn() {}
}
