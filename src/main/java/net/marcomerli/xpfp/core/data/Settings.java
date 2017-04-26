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

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Settings extends Data {

	private static final long serialVersionUID = 511605731858765879L;

	private static final String EXPORT_DIRECTORY = "export.directory";
	private static final String GEOAPI_KEY = "geoapi.key";
	private static final String PROXY_ACTIVE = "proxy.active";
	private static final String PROXY_HOSTNAME = "proxy.hostname";
	private static final String PROXY_PORT = "proxy.port";

	@Override
	protected File file()
	{
		return new File(dataDir, "settings.properties");
	}

	@Override
	protected void init()
	{
		setExportDirectory(System.getProperty("user.home"));
		setGeoApiKey("");
		setProxyActive(false);
		setProxyHostname("127.0.0.1");
		setProxyPort("3128");
	}

	public File getExportDirectory()
	{
		String dir = getProperty(EXPORT_DIRECTORY);
		return (StringUtils.isNotBlank(dir) ? new File(dir) : null);
	}

	public void setExportDirectory(String value)
	{
		setProperty(EXPORT_DIRECTORY, value);
	}

	public String getGeoApiKey()
	{
		return getProperty(GEOAPI_KEY);
	}

	public void setGeoApiKey(String value)
	{
		setProperty(GEOAPI_KEY, value);
	}

	public Boolean isProxyActive()
	{
		return new Boolean(getProperty(PROXY_ACTIVE));
	}

	public void setProxyActive(Boolean value)
	{
		setProperty(PROXY_ACTIVE, value.toString());
	}

	public String getProxyHostname()
	{
		return getProperty(PROXY_HOSTNAME);
	}

	public void setProxyHostname(String value)
	{
		setProperty(PROXY_HOSTNAME, value);
	}

	public Integer getProxyPort()
	{
		String port = getProperty(PROXY_PORT);
		return (StringUtils.isNotBlank(port) ? new Integer(port) : null);
	}

	public void setProxyPort(String value)
	{
		setProperty(PROXY_PORT, value);
	}
}
