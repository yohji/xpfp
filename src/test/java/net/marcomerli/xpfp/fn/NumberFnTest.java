package net.marcomerli.xpfp.fn;

import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;

public class NumberFnTest extends UnitTestSupport {

	@Test
	public void round()
	{
		assertEquals(0, NumberFn.round(0, 5));
		assertEquals(0, NumberFn.round(1, 5));
		assertEquals(0, NumberFn.round(2, 5));
		assertEquals(5, NumberFn.round(3, 5));
		assertEquals(5, NumberFn.round(4, 5));
		assertEquals(5, NumberFn.round(5, 5));

		assertEquals(100, NumberFn.round(99, 100));
		assertEquals(100, NumberFn.round(101, 100));
		assertEquals(100, NumberFn.round(149, 100));
		assertEquals(200, NumberFn.round(150, 100));
		assertEquals(200, NumberFn.round(151, 100));
		assertEquals(200, NumberFn.round(199, 100));

		assertEquals(- 750, NumberFn.round(- 749, 250));
		assertEquals(- 750, NumberFn.round(- 751, 250));
		assertEquals(- 1000, NumberFn.round(- 999, 250));
	}
}
