package net.marcomerli.xpfp.file.read;

import java.io.File;

import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.error.NoSuchWaypointException;
import net.marcomerli.xpfp.model.FlightPlan;
import net.marcomerli.xpfp.model.Location;
import net.marcomerli.xpfp.model.Waypoint;

public class FMSReaderTest extends UnitTestSupport {

	private static FlightPlan flightPlan;

	@Test
	public void sample01() throws Exception
	{
		_text("sample_01.fms");
	}

	@Test
	public void sample02() throws Exception
	{
		_text("sample_02.fms");
	}

	private void _text(String filename) throws Exception
	{
		File file = new File(this.getClass().getClassLoader().getResource(filename).getPath());
		flightPlan = new FMSReader(file).read();

		assertNotNull(flightPlan);
		assertStringNotBlank(flightPlan.getName());
		assertStringNotBlank(flightPlan.getFilename());

		try {
			flightPlan.getDeparture();
			flightPlan.getDestination();
		}
		catch (NoSuchWaypointException e) {
			failWhenExceptionNotExpected(e);
		}
		assertNotEmpty(flightPlan);
		for (Waypoint wp : flightPlan) {

			assertStringNotBlank(wp.getIdentifier());
			assertNotNull(wp.getType());

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
