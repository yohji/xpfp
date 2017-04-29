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

package net.marcomerli.xpfp.file.write;

import java.io.File;

import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Writer {

	protected File file;

	public Writer(File dir, String filename) {

		file = new File(dir, String.format("%s.%s",
			filename, type().extension()));
	}

	public abstract void write(FlightPlan flightPlan) throws Exception;

	protected abstract FileType type();

	public boolean exists()
	{
		return file.exists();
	}

	public boolean delete()
	{
		return file.delete();
	}
}
