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

package net.marcomerli.xpfp.core.data;

import java.io.File;

import net.marcomerli.xpfp.core.Context;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Settings extends Data {

	private static final long serialVersionUID = 511605731858765879L;

	public static final String EXPORT_DIRECTORY = "export.directory";
	public static final String GEOAPI_KEY = "geoapi.key";
	public static final String PROXY_ACTIVE = "proxy.active";
	public static final String PROXY_HOSTNAME = "proxy.hostname";
	public static final String PROXY_PORT = "proxy.port";
	public static final String PROXY_AUTH = "proxy.auth.active";
	public static final String PROXY_AUTH_USERNAME = "proxy.auth.username";
	public static final String PROXY_AUTH_PASSWORD = "proxy.auth.password";

	@Override
	protected File file()
	{
		return new File(Context.getHomedir(), "settings.properties");
	}

	@Override
	protected void init()
	{
		setProperty(EXPORT_DIRECTORY, System.getProperty("user.home"));
		setProperty(PROXY_ACTIVE, "false");
		setProperty(PROXY_HOSTNAME, "127.0.0.1");
		setProperty(PROXY_PORT, "3128");
		setProperty(PROXY_AUTH, "false");
	}

	@Override
	protected void upgrade()
	{}
}
