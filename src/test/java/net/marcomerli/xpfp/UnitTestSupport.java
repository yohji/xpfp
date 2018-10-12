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

package net.marcomerli.xpfp;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.marcomerli.xpfp.core.Context;

/**
 * @author Marco Merli
 * @since 1.0
 */
public abstract class UnitTestSupport extends Assert {

	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public UnitTestSupport() {

		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (StringUtils.isEmpty(proxyHost)) {
			proxyHost = System.getProperty("https.proxyHost");
			proxyPort = System.getProperty("https.proxyPort");
		}

		Proxy proxy = Proxy.NO_PROXY;
		if (StringUtils.isNotEmpty(proxyHost))
			proxy = new Proxy(Type.HTTP, new InetSocketAddress(
				proxyHost, Integer.valueOf(proxyPort)));

		Context.setProxy(proxy);
	}

	//
	// Support
	//

	protected static final Result run(Class<? extends UnitTestSupport> test)
	{
		return new JUnitCore().run(test);
	}

	//
	// Asserts
	//

	protected final void assertStringNotBlank(String string)
	{
		assertStringNotBlank(string, null);
	}

	protected final void assertStringNotBlank(String string, String format, Object... args)
	{
		assertFalse(parseMessage(format, args),
			StringUtils.isBlank(string));
	}

	protected final void assertNumberNotZero(Number number)
	{
		assertNumberNotZero(number, null);
	}

	protected final void assertNumberNotZero(Number number, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			number != null && number.longValue() != 0);
	}

	protected final void assertNumberBetween(Number number, Number low, Number high)
	{
		assertNumberBetween(number, low, high, null);
	}

	protected final void assertNumberBetween(Number number, Number low, Number high, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			number != null && number.longValue() >= low.longValue() && number.longValue() <= high.longValue());
	}

	protected final void assertNotEmpty(Object[] arr)
	{
		assertNotEmpty(arr, null);
	}

	protected final void assertNotEmpty(Object[] arr, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			arr != null && arr.length > 0);
	}

	protected final void assertNotEmpty(Collection<?> coll)
	{
		assertNotEmpty(coll, null);
	}

	protected final void assertNotEmpty(Collection<?> coll, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			coll != null && ! coll.isEmpty());
	}

	protected final void assertNotEmpty(Map<?, ?> map)
	{
		assertNotEmpty(map, null);
	}

	protected final void assertNotEmpty(Map<?, ?> map, String format, Object... args)
	{
		assertTrue(parseMessage(format, args),
			map != null && ! map.isEmpty());
	}

	protected final void failWhenExceptionExpected()
	{
		fail("Exception expected.");
	}

	protected final void failWhenExceptionNotExpected(Exception e)
	{
		logger.error(e.getMessage(), e);
		fail("Exception not expected.");
	}

	//
	// Print
	//

	protected final void println(Object object)
	{
		logger.info(object.toString());
	}

	protected final void printf(String format, Object... args)
	{
		logger.info(parseMessage(format, args));
	}

	protected final void printe(Throwable e)
	{
		logger.error(e.getMessage(), e);
	}

	//
	// Util
	//

	protected Date toDate(String yyyyMMdd) throws Exception
	{
		return df.parse(yyyyMMdd);
	}

	protected File tempDir()
	{
		return new File(System.getProperty("java.io.tmpdir"));
	}

	//
	// Internal
	//

	private String parseMessage(String format, Object... args)
	{
		String message = null;
		if (StringUtils.isNotBlank(format))
			message = String.format(format, args);

		return message;
	}
}
