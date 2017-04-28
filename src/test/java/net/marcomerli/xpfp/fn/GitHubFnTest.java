package net.marcomerli.xpfp.fn;

import org.junit.BeforeClass;
import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;

public class GitHubFnTest extends UnitTestSupport {

	@BeforeClass
	public static void before() throws Exception
	{
		GitHubFn.client.setProxy(proxy());
	}

	@Test
	public void createIssue()
	{
		try {
			assertTrue(GitHubFn.createIssue("TEST", "TEST"));
		}
		catch (Exception e) {
			failWhenExceptionNotExpected(e);
		}
	}
}
