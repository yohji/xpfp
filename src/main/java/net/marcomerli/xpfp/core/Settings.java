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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Settings extends Properties {

	private static final long serialVersionUID = 511605731858765879L;

	private static final File FILE = new File(new File(".")
		+ File.separator + "etc" + File.separator + "settings.properties");

	private static final String FMS_DIRECTORY = "fms.directory";
	private static final String PROXY_ACTIVE = "proxy.active";
	private static final String PROXY_HOSTNAME = "proxy.hostname";
	private static final String PROXY_PORT = "proxy.port";

	public Settings load() throws Exception
	{
		if (! FILE.exists())
			FILE.createNewFile();

		load(new FileReader(FILE));
		return this;
	}

	public Settings save() throws Exception
	{
		store(new FileWriter(FILE), "");
		return this;
	}

	public File getFMSDirectory()
	{
		String dir = getProperty(FMS_DIRECTORY);
		return (StringUtils.isNotBlank(dir) ? new File(dir) : null);
	}

	public void setFMSDirectory(String value)
	{
		setProperty(FMS_DIRECTORY, value);
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
