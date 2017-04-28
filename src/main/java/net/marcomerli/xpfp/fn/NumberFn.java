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

package net.marcomerli.xpfp.fn;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class NumberFn {

	private static final Map<Integer, DecimalFormat> formatters = new HashMap<>();

	public static String format(double number, int precision)
	{
		DecimalFormat df = formatters.get(precision);
		if (df == null)
			formatters.put(precision,
				(df = new DecimalFormat("#." + StringUtils.repeat('0', precision),
					new DecimalFormatSymbols(Locale.US))));

		return df.format(number);
	}

	public static boolean isNumeric(String number)
	{
		if (number == null)
			return false;

		return number.matches("-?\\d+([\\.,]\\d+)?");
	}

	private NumberFn() {}
}
