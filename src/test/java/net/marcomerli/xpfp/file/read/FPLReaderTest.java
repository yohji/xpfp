package net.marcomerli.xpfp.file.read;

import java.io.File;

import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.error.FlightPlanException;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;
import net.marcomerli.xpfp.model.WaypointType;

public class FPLReaderTest extends UnitTestSupport {

	private static FlightPlan flightPlan;

	@Test
	public void sample01() throws Exception
	{
		_text("sample_01.fpl");
	}

	@Test
	public void sample02() throws Exception
	{
		_text("sample_02.fpl");
	}

	private void _text(String filename) throws Exception
	{
		File file = new File(this.getClass().getClassLoader().getResource(filename).getPath());
		flightPlan = new FPLReader(file).read();

		assertNotNull(flightPlan);
		assertStringNotBlank(flightPlan.getName());
		assertStringNotBlank(flightPlan.getFilename());

		try {
			flightPlan.validate();
		}
		catch (FlightPlanException e) {
			failWhenExceptionNotExpected(e);
		}

		assertNotEmpty(flightPlan);
		for (Waypoint wp : flightPlan) {

			assertStringNotBlank(wp.getIdentifier());
			assertNotNull(wp.getType());
			if (! wp.getType().equals(WaypointType.POS))
				assertStringNotBlank(wp.getCountry());

			Location loc = wp.getLocation();

			assertNotNull(loc);
			assertNumberNotZero(loc.lat);
			assertNumberNotZero(loc.lng);
		}
	}

	public static FlightPlan getFlightPlan()
	{
		return flightPlan;
	}
}
