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

package net.marcomerli.xpfp.file;

import java.io.File;

import net.marcomerli.xpfp.file.read.FMSReader;
import net.marcomerli.xpfp.file.read.FPLReader;
import net.marcomerli.xpfp.file.read.Reader;

/**
 * @author Marco Merli
 * @since 1.0
 */
public enum FileType {

	FMS(FMSReader.class), // X-Plane
	FPL(FPLReader.class), // Garmin
	GPX(null), // GPS eXchange
	KML(null), // Google
	GML(null); // Geography Markup Language

	public static FileType get(File file)
	{
		String n = file.getName();
		String ext = n.substring(n.lastIndexOf('.') + 1);

		for (FileType ft : values())
			if (ft.extension().equalsIgnoreCase(ext))
				return ft;

		throw new IllegalArgumentException("File not supported: " + ext);
	}

	private Class<? extends Reader> reader;

	private FileType(Class<? extends Reader> reader) {

		this.reader = reader;
	}

	public String extension()
	{
		return name().toLowerCase();
	}

	public Class<? extends Reader> getReader()
	{
		return reader;
	}
}
