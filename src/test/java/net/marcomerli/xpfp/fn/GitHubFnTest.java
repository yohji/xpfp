package net.marcomerli.xpfp.fn;

import org.junit.Before;
import org.junit.Test;

import net.marcomerli.xpfp.UnitTestSupport;
import net.marcomerli.xpfp.core.Context;

public class GitHubFnTest extends UnitTestSupport {

	@Before
	public void before() throws Exception
	{
		GitHubFn.client.setProxy(Context.getProxy());
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
