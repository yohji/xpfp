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
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Data extends Properties {

	private static final long serialVersionUID = 8603751824062874788L;

	public void load()
	{
		if (! file().exists()) {
			init();
			save();
		}
		else {
			try {
				load(new FileReader(file()));
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void save()
	{
		try {
			store(new FileWriter(file()), "");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getProperty(String key)
	{
		return StringUtils.stripToEmpty(super.getProperty(key));
	}

	public <T> T getProperty(String key, Class<T> type)
	{
		try {
			Constructor<T> constr = type.getConstructor(String.class);
			return constr.newInstance(getProperty(key));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean hasProperty(String key)
	{
		return StringUtils.isNotBlank(super.getProperty(key));
	}

	protected abstract File file();

	protected abstract void init();
}
