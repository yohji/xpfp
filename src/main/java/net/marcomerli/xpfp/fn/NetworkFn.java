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

import net.marcomerli.xpfp.core.Context;
import net.marcomerli.xpfp.error.NetworkException;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class NetworkFn {

	private static final String TEST_ADDRESS = "www.google.com";
	private static final int TEST_TIMEOUT = 1000;

	public static void requireInternet() throws NetworkException
	{
		if (! hasInternet())
			throw new NetworkException("No internet connection available.");
	}

	public static boolean hasInternet()
	{
		try {
			InetAddress[] addresses = InetAddress.getAllByName(TEST_ADDRESS);
			if (addresses[0].isReachable(TEST_TIMEOUT))
				return true;

			new URL("https://" + TEST_ADDRESS)
				.openConnection(Context.getProxy())
				.connect();

			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	private NetworkFn() {}
}
