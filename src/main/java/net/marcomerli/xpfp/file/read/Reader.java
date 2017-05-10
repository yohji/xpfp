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
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.marcomerli.xpfp.error.ReaderException;
import net.marcomerli.xpfp.file.FileType;
import net.marcomerli.xpfp.model.FlightPlan;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class Reader {

	protected File file;

	public Reader(File file, FileType type) {

		String f = file.getName();
		Validate.isTrue(f.length() >= 4 && f.substring(f.length() - 3, f.length()).equalsIgnoreCase(type.extension()),
			"Invalid FPL file format.");

		this.file = file;
	}

	public abstract FlightPlan read() throws Exception;

	//
	// Validation
	//

	protected void validate(final boolean expression, String message, Object... args)
		throws ReaderException
	{
		if (! expression)
			throw new ReaderException(message, args);
	}

	protected void validateNot(final boolean expression, String message, Object... args)
		throws ReaderException
	{
		if (expression)
			throw new ReaderException(message, args);
	}

	protected void validateNull(final Object target, String message, Object... args)
		throws ReaderException
	{
		if (target == null)
			throw new ReaderException(message, args);
	}

	protected <T extends CharSequence> void validateBlank(final T target, String message, Object... args)
		throws ReaderException
	{
		if (StringUtils.isBlank(target))
			throw new ReaderException(message, args);
	}

	protected <T> void validateEmpty(final T[] target, String message, Object... args)
		throws ReaderException
	{
		if (target == null || target.length == 0)
			throw new ReaderException(message, args);
	}

	protected <T extends Map<?, ?>> void validateEmpty(final T target, String message, Object... args)
		throws ReaderException
	{
		if (target == null || target.isEmpty())
			throw new ReaderException(message, args);
	}

	protected <T extends Collection<?>> void validateEmpty(final T target, String message, Object... args)
		throws ReaderException
	{
		if (target == null || target.isEmpty())
			throw new ReaderException(message, args);
	}
}
