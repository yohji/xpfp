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
import java.util.Properties;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Data extends Properties {

	private static final long serialVersionUID = 8603751824062874788L;

	protected final File dataDir = new File(new File(".") + File.separator + "etc");

	public void load() throws Exception
	{
		if (! file().exists()) {
			init();
			save();
		}
		else
			load(new FileReader(file()));
	}

	public void save() throws Exception
	{
		store(new FileWriter(file()), "");
	}

	protected abstract File file();

	protected abstract void init();
}
