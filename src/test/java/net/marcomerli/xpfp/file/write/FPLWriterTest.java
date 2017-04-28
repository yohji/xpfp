package net.marcomerli.xpfp.file.write;

import java.io.File;

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
		File file = new File(tempDir(), fp.getFilename());

		try {
			new FMSWriter(file).write(fp);
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}
}
