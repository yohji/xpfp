package net.marcomerli.xpfp.file.write;

import org.junit.Before;
import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.file.read.FPLReaderTest;
import net.marcomerli.xpfp.model.FlightPlan;

public class FPLWriterTest extends UnitTestSupport {

	@Before
	public void before() throws Exception
	{
		run(FPLReaderTest.class);
	}

	@Test
	public void write() throws Exception
	{
		FlightPlan fp = FPLReaderTest.getFlightPlan();
		FMSWriter writer = new FMSWriter(tempDir(), fp.getFilename());

		assertFalse(writer.exists());

		try {
			writer.write(fp);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
		finally {
			writer.delete();
		}
	}
}
