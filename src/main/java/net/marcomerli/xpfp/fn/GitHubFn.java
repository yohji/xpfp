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

import java.net.CookieManager;
import java.net.CookiePolicy;

import org.apache.commons.lang3.Validate;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import net.marcomerli.xpfp.core.Context;

/**
 * @author Marco Merli
 * @since 1.0
 */
public class GitHubFn {

	private static final String URL = "https://api.github.com/repos/yohji/xpfp";
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	protected static final OkHttpClient client;
	static {
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

		client = new OkHttpClient();
		client.setCookieHandler(cookieManager);
	}

	public static void init()
	{
		client.setProxy(Context.getProxy());
	}

	public static final boolean createIssue(String title, String body)
		throws Exception
	{
		Validate.notBlank(title, "title cannot be null");
		Validate.notBlank(body, "body cannot be null");
		NetworkFn.requireInternet();

		String json = String.format("{\"title\":\"%s\",\"body\":\"%s\"}",
			title, body);

		Request request = new Request.Builder()
			.url(URL + "/issues")
			.addHeader("Accept", "application/vnd.github.v3+json")
			.post(RequestBody.create(JSON, json))
			.build();

		return client.newCall(request).execute().isSuccessful();
	}

	private GitHubFn() {}
}
