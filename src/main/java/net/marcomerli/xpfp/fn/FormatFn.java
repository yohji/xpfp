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

package net.marcomerli.xpfp.fn;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class FormatFn {

	public static String distance(Double distance)
	{
		if (distance == null || distance.longValue() == 0)
			return "-";

		return NumberFn.format(UnitFn.mToNn(distance), 2) + " nm";
	}

	public static String time(Long time)
	{
		if (time == null || time.longValue() == 0)
			return "-";

		return DurationFormatUtils.formatDuration(time, "HH:mm:ss");
	}

	public static String degree(Double bearing)
	{
		if (bearing == null)
			return "-";

		return String.format("%d°", Math.round(bearing));
	}

	private FormatFn() {}
}
