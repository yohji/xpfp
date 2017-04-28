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

package net.marcomerli.xpfp.file.read;

import java.io.File;

import org.apache.commons.lang3.Validate;

import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Reader {

	protected File file;

	public Reader(File file) {

		String f = file.getName();
		Validate.isTrue(f.length() >= 4 && f.substring(f.length() - 4, f.length()).equalsIgnoreCase(extension()),
			"Invalid FPL file format.");

		this.file = file;
	}

	public abstract FlightPlan read() throws Exception;

	public abstract String extension();
}
