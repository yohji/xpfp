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

package net.marcomerli.xpfp.core;

import java.util.Properties;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Settings extends Properties {

	private static final long serialVersionUID = 511605731858765879L;

	private static final String FMS_DIRECTORY = "fms.directory";
	private static final String PROXY_ACTIVE = "proxy.active";
	private static final String PROXY_HOSTNAME = "proxy.hostname";
	private static final String PROXY_PORT = "proxy.port";

	public String getFMSDirectory()
	{
		return getProperty(FMS_DIRECTORY);
	}

	public void setFMSDirectory(String value)
	{
		setProperty(FMS_DIRECTORY, value);
	}

	public Boolean isProxyHostname()
	{
		return new Boolean(getProperty(PROXY_ACTIVE));
	}

	public void setProxyHostname(Boolean value)
	{
		setProperty(PROXY_HOSTNAME, value.toString());
	}

	public String getProxyHostname()
	{
		return getProperty(PROXY_HOSTNAME);
	}

	public void setProxyHostname(String value)
	{
		setProperty(PROXY_HOSTNAME, value);
	}

	public String getProxyPort()
	{
		return getProperty(PROXY_PORT);
	}

	public void setProxyPort(String value)
	{
		setProperty(PROXY_PORT, value);
	}
}
