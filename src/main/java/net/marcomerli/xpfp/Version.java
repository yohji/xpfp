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

package net.marcomerli.xpfp;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class Version {

	private static final String POM_PROPS = "/META-INF/maven/net.marcomerli/xpfp/pom.properties";

	public static String get() throws Exception
	{
		try (InputStream in = Version.class.getResourceAsStream(POM_PROPS)) {
			if (in != null) {

				Properties props = new Properties();
				props.load(in);

				return props.getProperty("version");
			}
		}

		return "?.?";
	}

	private Version() {}
}
