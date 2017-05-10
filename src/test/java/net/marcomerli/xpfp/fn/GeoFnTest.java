package net.marcomerli.xpfp.fn;

import org.junit.BeforeClass;
import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.model.Location;

public class GeoFnTest extends UnitTestSupport {

	@BeforeClass
	public static void before() throws Exception
	{
		GeoFn.context.setApiKey("AIzaSyC96Ww5A-wNmGFLfVpbr61eLr_JZv9cjuQ");
		GeoFn.context.setProxy(proxy());
	}

	@Test
	public void distance()
	{
		Location a = new Location(45.086389, 7.601944);
		Location b = new Location(45.0601, 7.3886);
		double dist = GeoFn.distance(a, b);

		assertNumberNotZero(dist);
		assertNumberBetween(dist, 16999, 17010);
	}

	@Test
	public void point()
	{
		Location start = new Location(45.086389, 7.601944);
		Location expected = new Location(45.0601, 7.3886);
		Location actual = GeoFn.point(start, 17000, 260);

		assertNotNull(actual);
		assertNumberBetween(GeoFn.distance(expected, actual), 0, 100);
	}

	@Test
	public void elevation() throws Exception
	{
		Location loc = new Location(45.086389, 7.601944);
		GeoFn.elevation(loc);

		assertNumberNotZero(loc.alt);
		assertNumberBetween(loc.alt, 282, 284);
	}

	@Test
	public void elevations() throws Exception
	{
		double[] elevs = GeoFn.elevations(
			new Location(45.0146, 5.5828),
			new Location(44.5347, 6.1221));

		assertNotNull(elevs);
		assertNotEquals(0, elevs.length);

		for (int i = 0; i < elevs.length; i++)
			assertNumberNotZero(elevs[i]);
	}

	@Test
	public void declination() throws Exception
	{
		Location loc = new Location(45.0, 7.5);
		double decl = GeoFn.declination(loc);

		assertNumberNotZero(decl);
		assertNumberBetween(decl, 2, 3);

		assertNumberBetween(GeoFn.declination(new Location(30, - 30)), - 12, - 8);
		assertNumberBetween(GeoFn.declination(new Location(25, 0)), - 2, 2);
		assertNumberBetween(GeoFn.declination(new Location(55, - 135)), 15, 25);
		assertNumberBetween(GeoFn.declination(new Location(- 65, - 170)), 50, 70);
	}
}
