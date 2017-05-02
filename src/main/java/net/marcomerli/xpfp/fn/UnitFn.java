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

/**
 * @author Marco Merli
 * @since 1.0
 */
public class UnitFn {

	public static double mToNm(double unit)
	{
		return unit / 1852;
	}

	public static double nmToM(double unit)
	{
		return unit * 1852;
	}

	public static double mToFt(double unit)
	{
		return unit / 0.3048;
	}

	public static double ftToM(double unit)
	{
		return unit * 0.3048;
	}

	public static double msTKn(double unit)
	{
		return unit / 0.514;
	}

	public static double knToMs(double unit)
	{
		return unit * 0.514;
	}

	private UnitFn() {}
}
